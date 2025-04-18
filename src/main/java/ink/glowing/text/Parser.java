package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierGetter;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.replace.Replacer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TreeSet;
import java.util.function.Function;

import static ink.glowing.text.InkyMessage.*;
import static java.util.function.Function.identity;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

/**
 * Internal parser class for converting formatted text strings into styled components.
 * Handles placeholders, style modifiers, color codes, and replacement spots.
 */
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

    /**
     * Parses a text string into a styled component using the provided context.
     * @param textStr input text to parse
     * @param context build context containing resolver and state
     * @return component representing parsed text structure
     */
    static @NotNull Component parse(@NotNull String textStr, @NotNull BuildContext context) {
        if (textStr.isEmpty()) return empty();
        return new Parser(textStr, context.resolver()).parseRecursive(0, -1, context);
    }

    /**
     * Recursively parses text segments while handling nested syntax structures.
     * @param from starting index for parsing
     * @param untilCh stopping character
     * @param context current build context
     * @return component built from the parsed segment
     */
    private @NotNull Component parseRecursive(int from, int untilCh, @NotNull BuildContext context) {
        var builder = text();
        for (globalIndex = from; globalIndex < textLength; globalIndex++) {
            char ch = textStr.charAt(globalIndex);
            if (isSpecial(ch)) {
                if (isEscapedAt(textStr, globalIndex) || globalIndex + 1 >= textLength) continue;
                char nextCh = textStr.charAt(globalIndex + 1);
                switch (nextCh) {
                    case '[' -> { // &[...]
                        builder.append(parseSegment(from, globalIndex, context));
                        builder.append(parseRecursive(globalIndex + 2, ']', context));
                        from = globalIndex--;
                    }

                    case '{' -> { // &{placeholder} | &{placeholder:...}
                        int initIndex = globalIndex;
                        if (!iterateUntil(":}")) {
                            globalIndex = initIndex;
                            continue;
                        }
                        Placeholder placeholder = resolver.findPlaceholder(textStr.substring(initIndex + 2, globalIndex));
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
                        builder.append(prepareModifiers(context, placeholder).apply(placeholder.parse(params)));
                        from = globalIndex--;
                    }

                    case '(' -> { // &(...)
                        builder.append(parseSegment(from, globalIndex, context));
                        int initIndex = globalIndex;
                        var modifiers = prepareModifiers(context, resolver); // Also adjusts globalIndex to last modifier
                        if (textStr.charAt(globalIndex) != '[') {
                            globalIndex = initIndex;
                            continue;
                        }
                        builder.append(modifiers.apply(parseRecursive(globalIndex + 1, ']', context)));
                        from = globalIndex--;
                    }
                }
            } else if (ch == untilCh && isUnescapedAt(textStr, globalIndex)) {
                builder.append(parseSegment(from, globalIndex, context));
                return untilCh != ')'
                        ? prepareModifiers(context, resolver).apply(builder.build())
                        : builder.build();
            }
        }
        // In case we didn't find the 'untilCh' char
        builder.append(parseSegment(from, textLength, context));
        return builder.build();
    }

    /**
     * Processes a text segment between specified indices, applying symbolic styles and replacements.
     * @param from start index (inclusive)
     * @param until end index (exclusive)
     * @param context current build context
     * @return component representing the styled segment
     */
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

    /**
     * Helper method that appends unprocessed text between specified indices to the component builder.
     * @param builder target component builder
     * @param start start index (inclusive)
     * @param end end index (exclusive)
     * @param context current build context with style state
     */
    private void appendPrevious(@NotNull TextComponent.Builder builder, int start, int end, @NotNull BuildContext context) {
        if (start == end) return;
        builder.append(text(unescape(textStr.substring(start, end))).style(context.lastStyle()));
    }
    /**
     * Attempts to parse a hexadecimal color code from text input.
     * @param text hex color string
     * @param quirky whether to use Bungee format parsing (&x&1&2&3&4&5&6)
     * @return parsed TextColor or null if invalid
     */
    private static @Nullable TextColor parseHexColor(@NotNull String text, boolean quirky) {
        return quirky
                ? TextColor.fromHexString("#" +
                        text.charAt(2) + text.charAt(4) + text.charAt(6) +
                        text.charAt(8) + text.charAt(10) + text.charAt(12))
                : TextColor.fromCSSHexString(text);
    }

    /**
     * Finds the next replacement spot at the current parsing position.
     * @param index current text index
     * @param end maximum allowed end index for replacement
     * @return matching replacement spot or null if none
     */
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

    private @NotNull Function<Component, Component> prepareModifiers(
            @NotNull BuildContext context,
            @NotNull ModifierGetter modifierGetter
    ) {
        return prepareModifiers(identity(), context, modifierGetter);
    }

    /**
     * Parses and prepares style modifiers recursively.
     * @param comp component to modify
     * @param context current build context
     * @param modifierGetter source of style modifiers
     * @return modified component with all applicable styles
     */
    private @NotNull Function<Component, Component> prepareModifiers(
            @NotNull Function<Component, Component> comp,
            @NotNull BuildContext context,
            @NotNull ModifierGetter modifierGetter
    ) {
        int from = globalIndex + 1;
        if (from >= textLength || textStr.charAt(from) != '(') {
            globalIndex = from;
            return comp;
        }
        String modifierStr = extractPlain(from + 1, ":) ");
        Modifier<?> modifier = modifierGetter.findModifier(modifierStr);
        if (modifier == null) {
            globalIndex = from;
            return comp;
        }
        from = globalIndex + 1;
        if (globalIndex >= textLength || textStr.charAt(globalIndex) == ')') {
            if (modifier instanceof Modifier.Plain plainModifier) {
                comp = comp.andThen(prev -> plainModifier.modify(prev, "", ""));
            } else if (modifier instanceof Modifier.Complex complexModifier) {
                comp = comp.andThen(prev -> complexModifier.modify(prev, "", empty()));
            }
        } else {
            String params = "";
            if (textStr.charAt(globalIndex) == ':') {
                params = extractPlain(from, " )");
                from += params.length() + 1;
            }
            String finalParams = params;
            if (modifier instanceof Modifier.Plain plainModifier) {
                String value = extractPlainModifierValue(from);
                comp = comp.andThen(prev -> plainModifier.modify(prev, finalParams, unescape(value)));
            } else if (modifier instanceof Modifier.Complex complexModifier) {
                Component value = parseRecursive(from, ')', context.colorlessCopy());
                comp = comp.andThen(prev -> complexModifier.modify(prev, finalParams, value.compact()));
            }
        }
        return prepareModifiers(comp, context, modifierGetter);
    }

    /**
     * Extracts plain text until encountering any specified delimiter.
     * Also sets the globalIndex.
     * @param from start index for extraction
     * @param until set of stopping characters
     * @return extracted substring (excludes delimiters)
     */
    private @NotNull String extractPlain(int from, @NotNull String until) {
        globalIndex = from;
        if (iterateUntil(until)) {
            return textStr.substring(from, globalIndex);
        }
        return "";
    }

    /**
     * Extracts modifier value as plain text until closing parenthesis.
     * @param from start index for extraction
     * @return extracted value string
     */
    private @NotNull String extractPlainModifierValue(int from) {
        if (globalIndex != textLength && textStr.charAt(globalIndex) != ')') {
            globalIndex = from;
            if (iterateUntil(')')) {
                return textStr.substring(from, globalIndex);
            }
        }
        return "";
    }

    /**
     * Advances globalIndex until specified unescaped character is found.
     * @param until target character to find
     * @return true if character found, false otherwise
     */
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

    /**
     * Advances globalIndex until any of the specified characters is found.
     * @param until set of target characters
     * @return true if any character found, false otherwise
     */
    private boolean iterateUntil(@NotNull String until) {
        for (; globalIndex < textLength; globalIndex++) {
            if (until.indexOf(textStr.charAt(globalIndex)) != -1 && isUnescapedAt(textStr, globalIndex)) return true;
        }
        return false;
    }

    /**
     * Checks if a character is considered special (& or §).
     * @param ch character to check
     * @return true if character is part of syntax formatting
     */
    private static boolean isSpecial(char ch) {
        return ch == '&' || ch == '§';
    }
}
