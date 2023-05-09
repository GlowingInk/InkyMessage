package ink.glowing.text;

import ink.glowing.text.placeholders.Placeholder;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.style.tag.TagGetter;
import ink.glowing.text.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TreeSet;

import static ink.glowing.text.InkyMessage.*;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

final class Parser {
    private static final char END = 0;

    private final String textStr;
    private final int textLength;
    private final InkyMessage.Resolver resolver;
    private final TreeSet<Replacer.FoundSpot> replaceSpots;
    private int globalIndex;

    private Parser(@NotNull String textStr, @NotNull InkyMessage.Resolver resolver) {
        this.textStr = textStr;
        this.textLength = textStr.length();
        this.resolver = resolver;
        this.replaceSpots = resolver.matchReplacements(textStr);
    }

    public static @NotNull Component parse(@NotNull String textStr, @NotNull BuildContext context) {
        if (textStr.length() == 0) return empty();
        return new Parser(textStr, context.resolver())
                .parseRecursive(0, END, context);
    }

    private @NotNull Component parseRecursive(int from, char until, @NotNull BuildContext context) {
        var builder = text();
        for (globalIndex = from; globalIndex < textLength; globalIndex++) {
            char ch = textStr.charAt(globalIndex);
            if (isSpecial(ch)) {
                if (isEscapedAt(textStr, globalIndex) || globalIndex + 1 >= textLength) continue;
                char nextCh = textStr.charAt(globalIndex + 1);
                if (nextCh == '[') { // &[...]
                    builder.append(parseComponent(from, globalIndex, context));
                    builder.append(parseRecursive(globalIndex + 2, ']', context));
                    from = globalIndex--;
                } else if (nextCh == '{') { // &{ph} | &{ph:...}
                    int initIndex = globalIndex;
                    if (!iterateUntil(":}")) {
                        globalIndex = initIndex;
                        continue;
                    }
                    Placeholder placeholder = context.findPlaceholder(textStr.substring(initIndex + 2, globalIndex));
                    if (placeholder == null) {
                        globalIndex = initIndex;
                        continue;
                    }
                    String params;
                    if (textStr.charAt(globalIndex) == '}') {
                        params = "";
                    } else {
                        int paramsIndex = globalIndex += 1;
                        if (!iterateUntil('}')) {
                            globalIndex = initIndex;
                            continue;
                        }
                        params = textStr.substring(paramsIndex, globalIndex);
                    }
                    builder.append(parseComponent(from, initIndex, context));
                    builder.append(applyTags(placeholder.parse(params), context, placeholder));
                    from = globalIndex--;
                }
            } else if (ch == until && isUnescapedAt(textStr, globalIndex)) {
                builder.append(parseComponent(from, globalIndex, context));
                return until != ')'
                        ? applyTags(builder.build(), context, resolver)
                        : builder.build();
            }
        }
        // In a case we didn't find the 'until' char
        builder.append(parseComponent(from, textLength, context));
        return builder.build();
    }

    private @NotNull Component parseComponent(final int from, final int until, @NotNull BuildContext context) {
        if (from == until) return empty();
        var builder = text();
        int lastAppend = from;
        for (int index = from; index < until; index++) {
            var spot = matchSpot(index, until);
            if (spot != null) {
                appendPrevious(textStr, builder, lastAppend, index, context);
                builder.append(empty().style(context.lastStyle()).append(spot.replacement().get()));
                lastAppend = spot.end();
                index = lastAppend - 1;
                continue;
            }
            char ch = textStr.charAt(index);
            if (!isSpecial(ch)) continue;
            if (index + 1 == until || isEscapedAt(textStr, index)) continue;
            char styleCh = textStr.charAt(index + 1);
            switch (styleCh) {
                case '#', 'x' -> {
                    boolean quirky = styleCh == 'x';
                    int charsToSkip = quirky ? 14 : 8;
                    if (index + charsToSkip >= until) continue;
                    String colorStr = textStr.substring(index + 1, index + charsToSkip);
                    TextColor color = AdventureUtils.parseHexColor(colorStr, quirky);
                    if (color == null) continue;
                    appendPrevious(textStr, builder, lastAppend, index, context);
                    context.lastStyle(context.lastStyle().color(color));
                    lastAppend = (index += charsToSkip - 1) + 1;
                }
                default -> {
                    Style newStyle = resolver.applySymbolicStyle(styleCh, context.lastStyle());
                    if (newStyle == null) continue;
                    appendPrevious(textStr, builder, lastAppend, index, context);
                    context.lastStyle(newStyle);
                    lastAppend = (++index) + 1;
                }
            }
        }
        if (lastAppend < until) {
            appendPrevious(textStr, builder, lastAppend, until, context);
        }
        return builder.build();
    }

    private void appendPrevious(@NotNull String piece, @NotNull TextComponent.Builder builder, int start, int end, @NotNull BuildContext context) {
        if (start == end) return;
        String substring = unescape(piece.substring(start, end));
        builder.append(text(substring).style(context.lastStyle()));
    }

    private @Nullable Replacer.FoundSpot matchSpot(int index, int end) {
        while (!replaceSpots.isEmpty()) {
            var spot = replaceSpots.last();
            if (spot.start() == index) {
                replaceSpots.pollLast();
                return spot.end() > end ? null : spot;
            } else if (spot.start() < index) {
                replaceSpots.pollLast();
                continue;
            }
            break;
        }
        return null;
    }

    private @NotNull Component applyTags(
            @NotNull Component comp,
            @NotNull BuildContext context,
            @NotNull TagGetter tagGetter
    ) {
        int from = globalIndex + 1;
        if (from >= textLength || textStr.charAt(from) != '(') {
            globalIndex = from;
            return comp;
        }
        String tagStr = extractPlain(from + 1, ":) ");
        StyleTag<?> tag = tagGetter.findTag(tagStr);
        if (tag == null) {
            globalIndex = from;
            return comp;
        }
        from = globalIndex + 1;
        if (globalIndex >= textLength || textStr.charAt(globalIndex) == ')') {
            if (tag instanceof StyleTag.Plain plainTag) {
                comp = plainTag.modify(comp, "", "");
            } else if (tag instanceof StyleTag.Complex complexTag) {
                comp = complexTag.modify(comp, "", empty());
            }
        } else {
            String params = "";
            if (textStr.charAt(globalIndex) == ':') {
                params = extractPlain(from, " )");
                from += params.length() + 1;
            }
            if (tag instanceof StyleTag.Complex complexTag) {
                Component value = parseRecursive(from, ')', context.colorlessCopy());
                comp = complexTag.modify(comp, params, value.compact());
            } else if (tag instanceof StyleTag.Plain plainTag) {
                String value = extractPlainTagValue(from);
                comp = plainTag.modify(comp, params, unescape(value));
            }
        }
        return applyTags(comp, context, tagGetter);
    }

    private @NotNull String extractPlain(int from, @NotNull String until) {
        globalIndex = from;
        if (iterateUntil(until)) {
            return textStr.substring(from, globalIndex);
        }
        return "";
    }

    private @NotNull String extractPlainTagValue(int from) {
        if (globalIndex != textLength && textStr.charAt(globalIndex) != ')') {
            globalIndex = from;
            if (iterateUntil(')')) {
                return textStr.substring(from, globalIndex);
            }
        }
        return "";
    }

    private boolean iterateUntil(char until) {
        for (int index = textStr.indexOf(until, globalIndex); index >= 0; index = textStr.indexOf(until, index + 1)) {
            if (isUnescapedAt(textStr, index)) {
                globalIndex = index;
                return true;
            }
        }
        globalIndex = textLength;
        return false;
    }

    private boolean iterateUntil(@NotNull String until) {
        for (; globalIndex < textLength; globalIndex++) {
            if (until.indexOf(textStr.charAt(globalIndex)) != -1 && isUnescapedAt(textStr, globalIndex)) return true;
        }
        return false;
    }

    private static boolean isSpecial(char ch) {
        return ch == '&' || ch == '§';
    }
}
