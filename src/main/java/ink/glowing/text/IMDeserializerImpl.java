package ink.glowing.text;

import ink.glowing.text.replace.Replacer;
import ink.glowing.text.style.tag.StyleTag;
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

final class IMDeserializerImpl {
    private static final char END = 0;

    private final String textStr;
    private final boolean hasSlashes;
    private final InkyMessage.Resolver resolver;
    private final TreeSet<Replacer.FoundSpot> replaceSpots;
    private int globalIndex;

    private IMDeserializerImpl(@NotNull String textStr, @NotNull InkyMessage.Resolver resolver) {
        this.textStr = textStr;
        this.hasSlashes = textStr.indexOf('\\') != -1;
        this.resolver = resolver;
        this.replaceSpots = resolver.findReplacements(textStr);
    }

    public static @NotNull Component parse(@NotNull String textStr, @NotNull BuildContext context) {
        if (textStr.length() == 0) return empty();
        return new IMDeserializerImpl(textStr, context.resolver())
                .parseRecursive(0, END, context);
    }

    private @NotNull Component parseRecursive(int from, char until, @NotNull BuildContext context) {
        var builder = text();
        for (globalIndex = from; globalIndex < textStr.length(); globalIndex++) {
            char ch = textStr.charAt(globalIndex);
            if (isSpecial(ch)) {
                if (isEscapedAt(textStr, globalIndex) || globalIndex + 1 >= textStr.length()) continue;
                char nextCh = textStr.charAt(globalIndex + 1);
                if (nextCh == '[') {
                    builder.append(parseComponent(from, globalIndex, context));
                    builder.append(parseRecursive(globalIndex + 2, ']', context));
                    from = globalIndex;
                } /*else if (nextCh == '{') {
                    // TODO placeholders
                }*/
            } else if (ch == until && isUnescapedAt(textStr, globalIndex)) {
                builder.append(parseComponent(from, globalIndex, context));
                if (globalIndex + 1 >= textStr.length() || textStr.charAt(globalIndex + 1) != '(') {
                    return builder.build();
                }
                return applyTags(builder.build(), globalIndex + 2, context);
            }
        }
        // In a case when we didn't find the 'until' char
        builder.append(parseComponent(from, textStr.length(), context));
        return builder.build();
    }

    private @NotNull Component applyTags(@NotNull Component comp, int from, @NotNull BuildContext context) {
        StyleTag<?> tag = null;
        for (globalIndex = from; globalIndex < textStr.length(); globalIndex++) {
            char ch = textStr.charAt(globalIndex);
            if ("): ".indexOf(ch) != -1) { // My face when I'm rewriting parsing for the third time
                tag = resolver.getTag(textStr.substring(from, globalIndex));
                from = globalIndex + 1;
                break;
            }
        }
        if (globalIndex >= textStr.length() || textStr.charAt(globalIndex) == ')') {
            if (tag instanceof StyleTag.Plain plainTag) {
                comp = plainTag.modify(comp, "", "");
            } else if (tag instanceof StyleTag.Complex complexTag) {
                comp = complexTag.modify(comp, "", empty());
            }
        } else {
            String params = "";
            if (textStr.charAt(globalIndex) == ':') {
                for (globalIndex = from; globalIndex < textStr.length(); globalIndex++) {
                    if (" )".indexOf(textStr.charAt(globalIndex)) != -1) {
                        params = textStr.substring(from, globalIndex);
                        if (hasSlashes) params = unescape(params);
                        from = globalIndex + 1;
                        break;
                    }
                }
            }
            if (tag instanceof StyleTag.Plain plainTag) {
                String value = "";
                if (textStr.charAt(globalIndex) != ')') {
                    for (globalIndex = from; globalIndex < textStr.length(); globalIndex++) {
                        if (isUnescapedAt(')', textStr, globalIndex)) {
                            value = textStr.substring(from, globalIndex);
                            if (hasSlashes) value = unescape(value);
                            break;
                        }
                    }
                }
                comp = plainTag.modify(comp, params, value);
            } else if (tag instanceof StyleTag.Complex complexTag) {
                Component value = parseRecursive(from, ')', context.colorlessCopy());
                comp = complexTag.modify(comp, params, value.compact());
            }
        }
        if (++globalIndex < textStr.length() && textStr.charAt(globalIndex) == '(') {
            comp = applyTags(comp, globalIndex + 1, context);
        }
        return comp;
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
                case '#' -> {
                    if (index + 8 >= until) continue;
                    String colorStr = textStr.substring(index + 1, index + 8);
                    TextColor color = AdventureUtils.parseHexColor(colorStr, false);
                    if (color == null) continue;
                    appendPrevious(textStr, builder, lastAppend, index, context);
                    context.lastStyle(context.lastStyle().color(color));
                    lastAppend = (index += 7) + 1;
                }
                case 'x' -> {
                    if (index + 14 >= until) continue;
                    String colorStr = textStr.substring(index + 1, index + 14);
                    TextColor color = AdventureUtils.parseHexColor(colorStr, true);
                    if (color == null) continue;
                    appendPrevious(textStr, builder, lastAppend, index, context);
                    context.lastStyle(context.lastStyle().color(color));
                    lastAppend = (index += 13) + 1;
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
        String substring = piece.substring(start, end);
        if (hasSlashes) substring = unescape(substring);
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

    private static boolean isSpecial(char ch) {
        return ch == '&' || ch == '§';
    }
}
