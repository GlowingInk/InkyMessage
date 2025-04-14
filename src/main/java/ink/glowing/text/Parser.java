package ink.glowing.text;

import ink.glowing.text.placeholders.Placeholder;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.modifier.ModifierGetter;
import ink.glowing.text.modifier.StyleModifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TreeSet;

import static ink.glowing.text.InkyMessage.*;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

@ApiStatus.Internal
final class Parser {
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
        if (textStr.isEmpty()) return empty();
        return new Parser(textStr, context.resolver())
                .parseRecursive(0, -1, context);
    }

    private @NotNull Component parseRecursive(int from, int untilCh, @NotNull BuildContext context) {
        var builder = text();
        for (globalIndex = from; globalIndex < textLength; globalIndex++) {
            char ch = textStr.charAt(globalIndex);
            if (isSpecial(ch)) {
                if (isEscapedAt(textStr, globalIndex) || globalIndex + 1 >= textLength) continue;
                char nextCh = textStr.charAt(globalIndex + 1);
                if (nextCh == '[') { // &[...]
                    builder.append(parseSegment(from, globalIndex, context));
                    builder.append(parseRecursive(globalIndex + 2, ']', context));
                    from = globalIndex--;
                } else if (nextCh == '{') { // &{placeholder} | &{placeholder:...}
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
                    builder.append(parseSegment(from, initIndex, context));
                    builder.append(applyModifiers(placeholder.parse(params), context, placeholder));
                    from = globalIndex--;
                }
            } else if (ch == untilCh && isUnescapedAt(textStr, globalIndex)) {
                builder.append(parseSegment(from, globalIndex, context));
                return untilCh != ')'
                        ? applyModifiers(builder.build(), context, resolver)
                        : builder.build();
            }
        }
        // In a case we didn't find the 'untilCh' char
        builder.append(parseSegment(from, textLength, context));
        return builder.build();
    }

    private @NotNull Component parseSegment(int from, int until, @NotNull BuildContext context) {
        if (from == until) return empty();
        var builder = text();
        int lastAppend = from;
        for (int index = from; index < until; index++) {
            var spot = matchSpot(index, until);
            if (spot != null) {
                appendPrevious(builder, lastAppend, index, context);
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
                    int charsToSkip = quirky ? 14 : 8; // &x&1&2&3&4&5&6 | &#123456
                    if (index + charsToSkip >= until) continue;
                    String colorStr = textStr.substring(index + 1, index + charsToSkip);
                    TextColor color = parseHexColor(colorStr, quirky);
                    if (color == null) continue;
                    appendPrevious(builder, lastAppend, index, context);
                    context.lastStyle(context.lastStyle().color(color));
                    lastAppend = (index += charsToSkip - 1) + 1;
                }
                default -> {
                    Style newStyle = resolver.applySymbolicStyle(styleCh, context.lastStyle());
                    if (newStyle == null) continue;
                    appendPrevious(builder, lastAppend, index, context);
                    context.lastStyle(newStyle);
                    lastAppend = (++index) + 1;
                }
            }
        }
        if (lastAppend < until) {
            appendPrevious(builder, lastAppend, until, context);
        }
        return builder.build();
    }

    private void appendPrevious(@NotNull TextComponent.Builder builder, int start, int end, @NotNull BuildContext context) {
        if (start == end) return;
        builder.append(text(unescape(textStr.substring(start, end))).style(context.lastStyle()));
    }

    private static @Nullable TextColor parseHexColor(@NotNull String text, boolean quirky) {
        return quirky
                ? TextColor.fromHexString("#" +
                        text.charAt(2) + text.charAt(4) + text.charAt(6) +
                        text.charAt(8) + text.charAt(10) + text.charAt(12))
                : TextColor.fromCSSHexString(text);
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

    private @NotNull Component applyModifiers(
            @NotNull Component comp,
            @NotNull BuildContext context,
            @NotNull ModifierGetter modifierGetter
    ) {
        int from = globalIndex + 1;
        if (from >= textLength || textStr.charAt(from) != '(') {
            globalIndex = from;
            return comp;
        }
        String modifierStr = extractPlain(from + 1, ":) ");
        StyleModifier<?> modifier = modifierGetter.findModifier(modifierStr);
        if (modifier == null) {
            globalIndex = from;
            return comp;
        }
        from = globalIndex + 1;
        if (globalIndex >= textLength || textStr.charAt(globalIndex) == ')') {
            if (modifier instanceof StyleModifier.Plain plainModifier) {
                comp = plainModifier.modify(comp, "", "");
            } else if (modifier instanceof StyleModifier.Complex complexModifier) {
                comp = complexModifier.modify(comp, "", empty());
            }
        } else {
            String params = "";
            if (textStr.charAt(globalIndex) == ':') {
                params = extractPlain(from, " )");
                from += params.length() + 1;
            }
            if (modifier instanceof StyleModifier.Complex complexModifier) {
                Component value = parseRecursive(from, ')', context.colorlessCopy());
                comp = complexModifier.modify(comp, params, value.compact());
            } else if (modifier instanceof StyleModifier.Plain plainModifier) {
                String value = extractPlainModifierValue(from);
                comp = plainModifier.modify(comp, params, unescape(value));
            }
        }
        return applyModifiers(comp, context, modifierGetter);
    }

    private @NotNull String extractPlain(int from, @NotNull String until) {
        globalIndex = from;
        if (iterateUntil(until)) {
            return textStr.substring(from, globalIndex);
        }
        return "";
    }

    private @NotNull String extractPlainModifierValue(int from) {
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
