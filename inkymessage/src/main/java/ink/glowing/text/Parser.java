package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierFinder;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.placeholder.PlaceholderFinder;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.symbolic.SymbolicStyle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TreeSet;
import java.util.function.Function;

import static ink.glowing.text.InkyMessage.*;
import static ink.glowing.text.modifier.ModifierFinder.composeModifierFinders;
import static java.util.function.Function.identity;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

/**
 * Internal parser class for converting formatted text strings into styled components.
 * Handles placeholders, style modifiers, color codes, and replacement spots.
 */
@ApiStatus.Internal
final class Parser { // TODO This is a mess. Tokenizer?
    private final String textStr;
    private final int textLength;
    private final TreeSet<Replacer.FoundSpot> replaceSpots;

    private int globalIndex;

    private Parser(@NotNull String textStr, @NotNull TreeSet<Replacer.FoundSpot> replaceSpots) {
        this.textStr = textStr;
        this.textLength = textStr.length();
        this.replaceSpots = replaceSpots;
    }

    /**
     * Parses a text string into a styled component using the provided context.
     * @param textStr input text to parse
     * @param context build context containing context and state
     * @return component representing parsed text structure
     */
    static @NotNull Component parse(@NotNull String textStr, @NotNull Context context) {
        if (textStr.isEmpty()) return empty();
        return new Parser(textStr, context.matchReplacements(textStr))
                .parseRecursive(0, -1, context)
                .asComponent();
    }

    /**
     * Recursively parses text segments while handling nested syntax structures.
     * @param from starting index for parsing
     * @param untilCh stopping character
     * @param context current build context
     * @return component built from the parsed segment
     */
    private @NotNull ComponentLike parseRecursive(int from, int untilCh, @NotNull Context context) {
        var builder = text();
        for (globalIndex = from; globalIndex < textLength; globalIndex++) {
            char ch = textStr.charAt(globalIndex);
            if (isSpecial(ch)) {
                if (isEscapedAt(textStr, globalIndex) || globalIndex + 1 >= textLength) continue;
                char nextCh = textStr.charAt(globalIndex + 1);
                switch (nextCh) {
                    case '[' -> { // &[...]
                        appendSegment(builder, from, globalIndex, context);
                        builder.append(parseRecursive(globalIndex + 2, ']', context));
                        from = globalIndex--;
                    }

                    case '{' -> { // &{placeholder} | &{placeholder:...}
                        int initIndex = globalIndex;
                        var placeholderData = parsePlaceholder(context);
                        if (placeholderData == null) {
                            continue;
                        }
                        var placeholder = placeholderData.placeholder;
                        appendSegment(builder, from, initIndex, context);
                        builder.append(prepareModifiers(
                                context,
                                composeModifierFinders(context, placeholder::findLocalModifier)
                        ).apply(placeholder.parse(placeholderData.params)).applyFallbackStyle(context.lastStyle()));
                        from = globalIndex--;
                    }

                    case '(' -> { // &(...)
                        appendSegment(builder, from, globalIndex, context);
                        int initIndex = globalIndex;
                        var modifiers = prepareModifiers(context, context); // Also adjusts globalIndex to last modifier
                        if (textStr.charAt(globalIndex) != '[') {
                            globalIndex = initIndex;
                            continue;
                        }
                        builder.append(modifiers.apply(parseRecursive(globalIndex + 1, ']', context).asComponent()));
                        from = globalIndex--;
                    }

                    case '<' -> { // &<...>
                        appendSegment(builder, from, globalIndex, context);
                        int initIndex = globalIndex;
                        if (!iterateUntil('>')) {
                            globalIndex = initIndex;
                            continue;
                        }
                        String sub = unescape(textStr.substring(initIndex + 2, globalIndex));
                        builder.append(text(sub));
                        from = globalIndex + 1;
                    }
                }
            } else if (ch == untilCh && isUnescapedAt(textStr, globalIndex)) {
                appendSegment(builder, from, globalIndex, context);
                return untilCh != ')'
                        ? prepareModifiers(context, context).apply(builder.build())
                        : builder;
            }
        }
        // In case we didn't find the 'untilCh' char
        appendSegment(builder, from, textLength, context);
        return builder;
    }

    /**
     * Processes a text segment between specified indices, applying symbolic styles and replacements.
     * @param from start index (inclusive)
     * @param until end index (exclusive)
     * @param context current build context
     */
    private void appendSegment(@NotNull TextComponent.Builder builder, int from, int until, @NotNull Context context) {
        if (from == until) return;
        int lastAppend = from;
        for (int index = from; index < until; index++) {
            var spot = matchSpot(index, until);
            if (spot != null) {
                appendPrevious(builder, lastAppend, index, context);
                var replacement = spot.replacement().get();
                builder.append(replacement.applyFallbackStyle(context.lastStyle()));
                lastAppend = spot.end();
                index = lastAppend - 1;
                continue;
            }
            char ch = textStr.charAt(index);
            if (!isSpecial(ch) || index + 1 == until || isEscapedAt(textStr, index)) continue;
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
                    SymbolicStyle symbolic = context.findSymbolicStyle(styleCh);
                    if (symbolic == null) continue;
                    appendPrevious(builder, lastAppend, index, context);
                    context.lastStyle(symbolic.merge(context.lastStyle()));
                    lastAppend = (++index) + 1;
                }
            }
        }
        if (lastAppend < until) {
            appendPrevious(builder, lastAppend, until, context);
        }
    }

    /**
     * Helper method that appends unprocessed text between specified indices to the component builder.
     * @param builder target component builder
     * @param start start index (inclusive)
     * @param end end index (exclusive)
     * @param context current build context with style state
     */
    private void appendPrevious(@NotNull TextComponent.Builder builder, int start, int end, @NotNull Context context) {
        if (start == end) return;
        builder.append(text(unescape(textStr.substring(start, end))).applyFallbackStyle(context.lastStyle()));
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
                        text.charAt(2) + text.charAt(4) +
                        text.charAt(6) + text.charAt(8) +
                        text.charAt(10) + text.charAt(12))
                : TextColor.fromCSSHexString(text);
    }

    /**
     * Finds the next replacement spot at the current parsing position.
     * @param index current text index
     * @param end maximum allowed end index for replacement
     * @return matching replacement spot or null if none
     */
    private @Nullable Replacer.FoundSpot matchSpot(int index, int end) {
        for (var iterator = replaceSpots.descendingIterator(); iterator.hasNext();) {
            var spot = iterator.next();
            if (spot.start() == index) {
                iterator.remove();
                return spot.end() <= end ? spot : null;
            } else if (spot.start() < index) {
                iterator.remove();
            } else {
                break;
            }
        }
        return null;
    }

    private @NotNull Function<Component, Component> prepareModifiers(
            @NotNull Context context,
            @NotNull ModifierFinder modifierFinder
    ) {
        return prepareModifiers(identity(), context, modifierFinder);
    }

    /**
     * Parses and prepares style modifiers recursively.
     * @param comp component to modify
     * @param context current build context
     * @param modifierFinder source of style modifiers
     * @return modified component with all applicable styles
     */
    private @NotNull Function<Component, Component> prepareModifiers(
            @NotNull Function<Component, Component> comp,
            @NotNull Context context,
            @NotNull ModifierFinder modifierFinder
    ) {
        int from = globalIndex + 1;
        if (from >= textLength || textStr.charAt(from) != '(') {
            globalIndex = from;
            return comp;
        }
        String modifierStr = extractPlain(from + 1, ":) ");
        Modifier modifier = modifierFinder.findModifier(modifierStr);
        if (modifier == null) {
            globalIndex = from;
            return comp;
        }
        from = globalIndex + 1;

        var input = prepareInput(from, context);
        comp = comp.andThen(modifier.prepareModify(input));
        return prepareModifiers(comp, context, modifierFinder);
    }

    private Modifier.Tokens prepareInput(int from, Context context) {
        if (globalIndex >= textLength) return EmptyParametersImpl.FULLY_EMPTY;
        if (textStr.charAt(globalIndex) != ')' && textStr.charAt(globalIndex) == ':') {
            String params = extractPlain(from, " )");
            globalIndex = from + params.length();
            if (textStr.charAt(globalIndex) == ')') {
                return new EmptyParametersImpl(params);
            } else {
                return new ParametersImpl(params, context);
            }
        } else {
            return new ParametersImpl("", context);
        }
    }

    private record EmptyParametersImpl(@NotNull String parameter) implements Modifier.Tokens {
        static final Modifier.Tokens FULLY_EMPTY = new EmptyParametersImpl("");

        @Override
        public @Nullable String nextString() {
            return null;
        }

        @Override
        public @NotNull String remainingString() {
            return "";
        }

        @Override
        public @Nullable Component nextComponent() {
            return null;
        }

        @Override
        public @NotNull Component remainingComponent() {
            return empty();
        }

        @Override
        public boolean hasMore() {
            return false;
        }
    }

    private class ParametersImpl implements Modifier.Tokens {
        final String param;
        final Context context;
        boolean more;

        ParametersImpl(String param, Context context) {
            this.param = param;
            this.context = context;
            this.more = true;

            globalIndex++;
        }

        @Override
        public @NotNull String parameter() {
            return param;
        }

        @Override
        public @Nullable String nextString() {
            if (!more) return null;
            int from = textStr.charAt(globalIndex) == '&' && globalIndex + 1 < textLength && textStr.charAt(globalIndex + 1) == '<'
                    ? globalIndex + 1
                    : globalIndex;
            if (from >= textLength || textStr.charAt(from) == ')') {
                char result = textStr.charAt(globalIndex);
                globalIndex = from;
                more = false;
                return Character.toString(result);
            }
            char until = textStr.charAt(from) == '<'
                    ? '>'
                    : ' ';
            return postCheck(extractPlainModifierValue(from, until, context));
        }

        @Override
        public @NotNull String remainingString() {
            if (!more || globalIndex >= textLength) return "";
            return postCheck(extractPlainModifierValue(globalIndex, ')', context));
        }

        @Override
        public @Nullable Component nextComponent() {
            if (!more) return null;
            int from = textStr.charAt(globalIndex) == '&' && globalIndex + 1 < textLength && textStr.charAt(globalIndex + 1) == '['
                    ? globalIndex + 1
                    : globalIndex;
            if (from >= textLength || textStr.charAt(from) == ')') {
                char result = textStr.charAt(globalIndex);
                globalIndex = from;
                more = false;
                return text(result);
            }
            char until = textStr.charAt(from) == '['
                    ? ']'
                    : ' ';
            return postCheck(parseRecursive(from, until, context.stylelessCopy()).asComponent());
        }

        @Override
        public @NotNull Component remainingComponent() {
            if (!more || globalIndex >= textLength) return empty();
            return postCheck(parseRecursive(globalIndex, ')', context.stylelessCopy()).asComponent());
        }

        @Override
        public boolean hasMore() {
            return more;
        }

        private <T> T postCheck(T t) {
            if (textStr.charAt(globalIndex) != ' ') {
                more = false;
            }
            return t;
        }
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
            return unescape(textStr.substring(from, globalIndex));
        }
        return "";
    }

    /**
     * Extracts modifier value as plain text until closing parenthesis.
     * @param from start index for extraction
     * @return extracted value string
     */
    private @NotNull String extractPlainModifierValue(int from, char until, @NotNull Context context) {
        if (globalIndex != textLength && textStr.charAt(globalIndex) != ')') {
            StringBuilder builder = new StringBuilder();
            globalIndex = from;
            int lastIndex = globalIndex;
            for (;globalIndex < textLength; globalIndex++) {
                char ch = textStr.charAt(globalIndex);
                if (ch == until) {
                    if (isUnescapedAt(textStr, globalIndex)) {
                        return builder.append(textStr, lastIndex, globalIndex).toString();
                    }
                } else if (isSpecial(ch) && isUnescapedAt(textStr, globalIndex)) {
                    int initIndex = globalIndex;
                    PlaceholderData data = parsePlaceholder(context);
                    if (data != null) {
                        builder.append(textStr, lastIndex, initIndex);
                        builder.append(data.placeholder.parseInlined(data.params));
                        lastIndex = globalIndex + 1;
                    }
                }
            }
        }
        return "";
    }

    private @Nullable PlaceholderData parsePlaceholder(PlaceholderFinder placeholders) {
        int initIndex = globalIndex;
        if (!iterateUntil(":}")) {
            globalIndex = initIndex;
            return null;
        }
        Placeholder placeholder = placeholders.findPlaceholder(unescape(textStr.substring(initIndex + 2, globalIndex)));
        if (placeholder == null) {
            globalIndex = initIndex;
            return null;
        }
        String params;
        if (textStr.charAt(globalIndex) == '}') {
            params = "";
        } else {
            int paramsIndex = (++globalIndex);
            if (!iterateUntil('}')) {
                globalIndex = initIndex;
                return null;
            }
            params = unescape(textStr.substring(paramsIndex, globalIndex));
        }
        return new PlaceholderData(placeholder, params);
    }

    private record PlaceholderData(@NotNull Placeholder placeholder, @NotNull String params) { }

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
