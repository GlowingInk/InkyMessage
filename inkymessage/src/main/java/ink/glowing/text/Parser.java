package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.Modifier.ArgumentValue;
import ink.glowing.text.modifier.Modifier.Arguments;
import ink.glowing.text.modifier.ModifierFinder;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.placeholder.PlaceholderFinder;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.symbolic.SymbolicStyle;
import ink.glowing.text.utils.TextUtils;
import ink.glowing.text.utils.function.CharPredicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;

import static ink.glowing.text.InkyMessage.*;
import static ink.glowing.text.modifier.Modifier.ArgumentValue.argumentValue;
import static ink.glowing.text.modifier.Modifier.Arguments.arguments;
import static ink.glowing.text.modifier.ModifierFinder.composeModifierFinders;
import static ink.glowing.text.utils.TextUtils.indexOf;
import static java.util.function.Function.identity;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

/**
 * Internal parser class for converting formatted text strings into styled components.
 * Handles placeholders, style modifiers, color codes, and replacement spots.
 */
@ApiStatus.Internal
final class Parser { // TODO Should probably be refactored as a token-based parser?
    private static final CharPredicate SEGMENT_END = CharPredicate.of(']');
    private static final CharPredicate MODIFIER_END = CharPredicate.of(')');

    private final String textStr;
    private final char[] textArr;
    
    private final int textLength;
    private final Context context;
    private final TreeSet<Replacer.FoundSpot> replaceSpots;

    private int globalIndex;

    private Parser(@NotNull String textStr, @NotNull Context context) {
        this.textStr = textStr;
        this.textArr = textStr.toCharArray();
        this.textLength = textArr.length;
        this.context = context;
        this.replaceSpots = context.matchReplacements(textStr);
    }

    /**
     * Parses a text string into a styled component using the provided context.
     * @param textStr input text to parse
     * @param context build context containing context and state
     * @return component representing parsed text structure
     */
    static @NotNull Component parse(@NotNull String textStr, @NotNull Context context) {
        if (textStr.isEmpty()) return empty();
        return new Parser(textStr, context)
                .parseRecursive(0, CharPredicate.FALSE, new LinearState())
                .asComponent();
    }

    private static class LinearState {
        Style lastStyle;

        LinearState() {
            this.lastStyle = Style.empty();
        }
    }

    /**
     * Recursively parses text segments while handling nested syntax structures.
     * @param from starting index for parsing
     * @param untilCh stopping condition
     * @param state current state
     * @return component built from the parsed segment
     */
    private @NotNull ComponentLike parseRecursive(int from, CharPredicate untilCh, @NotNull LinearState state) {
        var builder = text();
        for (globalIndex = from; globalIndex < textLength; globalIndex++) {
            char ch = textArr[globalIndex];
            if (isSpecial(ch)) {
                if (isEscapedAt(textArr, globalIndex) || globalIndex + 1 >= textLength) continue;
                char nextCh = textArr[globalIndex + 1];
                switch (nextCh) {
                    case '[' -> { // Segment &[...]
                        appendSegment(builder, from, globalIndex, state);
                        builder.append(parseRecursive(globalIndex + 2, SEGMENT_END, state));
                        from = globalIndex--;
                    }

                    case '{' -> { // Placeholder &{...}
                        int initIndex = globalIndex;
                        var placeholderData = parsePlaceholder(context);
                        if (placeholderData == null) {
                            continue;
                        }
                        var placeholder = placeholderData.placeholder;
                        appendSegment(builder, from, initIndex, state);
                        builder.append(prepareModifiers(
                                composeModifierFinders(context, placeholder::findLocalModifier)
                        ).apply(placeholder.retrieve(placeholderData.params, context)).applyFallbackStyle(state.lastStyle));
                        from = globalIndex--;
                    }

                    case '(' -> { // Modifier &(...)
                        appendSegment(builder, from, globalIndex, state);
                        int initIndex = globalIndex;
                        var modifiers = prepareModifiers(context); // Also adjusts globalIndex to last modifier
                        if (textArr[globalIndex] == '[') { // Modifier+Segment &(...)[...]
                            builder.append(modifiers.apply(parseRecursive(globalIndex + 1, SEGMENT_END, state).asComponent()));
                            from = globalIndex--;
                            continue;
                        } if (textArr[globalIndex] == '{') { // Modifier+Placeholder &(...){...}
                            var placeholderData = parsePlaceholder(context);
                            if (placeholderData != null) {
                                var placeholder = placeholderData.placeholder;
                                builder.append(prepareModifiers(
                                        composeModifierFinders(context, placeholder::findLocalModifier)
                                ).apply(placeholder.retrieve(placeholderData.params, context)).applyFallbackStyle(state.lastStyle));
                                from = globalIndex--;
                                continue;
                            }
                        }
                        globalIndex = initIndex;
                    }
                }
            } else if (untilCh.test(ch) && isUnescapedAt(textArr, globalIndex)) {
                appendSegment(builder, from, globalIndex, state);
                return prepareModifiers(context).apply(builder.build()); // TODO Check if parsing modifiers
            }
        }
        // In case we didn't find the 'untilCh' char
        appendSegment(builder, from, textLength, state);
        return builder;
    }

    /**
     * Processes a text segment between specified indices, applying symbolic styles and replacements.
     * @param from start index (inclusive)
     * @param until end index (exclusive)
     * @param state current build state
     */
    private void appendSegment(@NotNull TextComponent.Builder builder, int from, int until, @NotNull LinearState state) {
        if (from == until) return;
        int lastAppend = from;
        for (int index = from; index < until; index++) {
            var spot = matchSpot(index, until);
            if (spot != null) {
                appendPrevious(builder, lastAppend, index, state);
                var replacement = spot.replacement().apply(spot.count());
                builder.append(replacement.applyFallbackStyle(state.lastStyle));
                lastAppend = spot.end();
                index = lastAppend - 1;
                continue;
            }
            char ch = textArr[index];
            if (!isSpecial(ch) || index + 1 == until || isEscapedAt(textArr, index)) continue;
            char styleCh = textArr[index + 1];
            switch (styleCh) {
                case '#', 'x' -> {
                    boolean quirky = styleCh == 'x';
                    int end = index + (quirky ? 14 : 8);
                    if (end >= until) continue;
                    TextColor color = quirky
                            ? parseQuirkyColor(TextUtils.subarray(textArr, index + 1, end))
                            : TextColor.fromCSSHexString(textStr.substring(index + 1, end));
                    if (color == null) continue;
                    appendPrevious(builder, lastAppend, index, state);
                    state.lastStyle = state.lastStyle.color(color);
                    index = (lastAppend = end) - 1;
                }
                default -> {
                    SymbolicStyle symbolic = context.findSymbolicStyle(styleCh);
                    if (symbolic == null) continue;
                    appendPrevious(builder, lastAppend, index, state);
                    state.lastStyle = symbolic.merge(state.lastStyle);
                    lastAppend = (++index) + 1;
                }
            }
        }
        if (lastAppend < until) {
            appendPrevious(builder, lastAppend, until, state);
        }
    }

    /**
     * Helper method that appends unprocessed text between specified indices to the component builder.
     * @param builder target component builder
     * @param start start index (inclusive)
     * @param end end index (exclusive)
     * @param state current build state
     */
    private void appendPrevious(@NotNull TextComponent.Builder builder, int start, int end, @NotNull LinearState state) {
        if (start == end) return;
        builder.append(text(unescape(textArr, start, end)).applyFallbackStyle(state.lastStyle));
    }

    private static @Nullable TextColor parseQuirkyColor(char @NotNull [] color) {
        return TextColor.fromHexString("#" + color[2] + color[4] + color[6] + color[8] + color[10] + color[12]);
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
                if (spot.end() <= end) return spot;
            } else if (spot.start() < index) {
                iterator.remove();
            } else {
                break;
            }
        }
        return null;
    }

    private @NotNull Function<Component, Component> prepareModifiers(
            @NotNull ModifierFinder modifierFinder
    ) {
        return prepareModifiers(identity(), modifierFinder);
    }

    private static final CharPredicate MOD_CHECKPOINT = CharPredicate.anyOf(":) ");
    /**
     * Parses and prepares style modifiers recursively.
     * @param comp component to modify
     * @param modifierFinder source of style modifiers
     * @return modified component with all applicable styles
     */
    private @NotNull Function<Component, Component> prepareModifiers(
            @NotNull Function<Component, Component> comp,
            @NotNull ModifierFinder modifierFinder
    ) {
        int from = globalIndex + 1;
        if (from >= textLength || textArr[from] != '(') {
            globalIndex = from;
            return comp;
        }
        String modifierStr = plainText(from + 1, MOD_CHECKPOINT);
        Modifier modifier = modifierFinder.findModifier(modifierStr);
        if (modifier == null) {
            globalIndex = from;
            return comp;
        }
        from = globalIndex + 1;

        var arguments = prepareArguments(from, modifier);
        if (arguments == null) {
            globalIndex = from;
            return comp;
        }
        comp = comp.andThen(modifier.prepareModification(arguments, context));
        return prepareModifiers(comp, modifierFinder);
    }

    private static final CharPredicate ARG_CHECKPOINT = CharPredicate.anyOf(" )");
    private @Nullable Arguments prepareArguments(int from, Modifier modifier) {
        if (globalIndex >= textLength) return Arguments.empty();
        String param;
        if (textArr[globalIndex] != ')' && textArr[globalIndex] == ':') {
            param = plainText(from, ARG_CHECKPOINT);
            globalIndex = from + param.length();
            if (textArr[globalIndex] == ')') {
                return arguments(param);
            }
        } else {
            param = "";
        }
        if (textArr[globalIndex] != ' ') {
            return null;
        }
        globalIndex++;
        char ch = textArr[globalIndex];
        if (ch == '[' || ch == '<') {
            List<ArgumentValue> list = new ArrayList<>();
            collectArguments(list, modifier.unknownArgumentType(param) == ArgumentValue.Type.STRING);
            return arguments(param, list);
        } else {
            var arguments = arguments(
                    param,
                    List.of(argumentValue(parseRecursive(
                            globalIndex,
                            MODIFIER_END,
                            new LinearState()
                    ).asComponent()))
            );
            globalIndex--;
            return arguments;
        }
    }

    private static final CharPredicate PLAIN_ARG_CHECKPOINT = CharPredicate.anyOf(">)");
    private static final CharPredicate UNK_ARG_CHECKPOINT = CharPredicate.anyOf(" )");
    private void collectArguments(List<ArgumentValue> list, boolean unknownAsString) {
        if (globalIndex >= textLength) return;
        char ch = textArr[globalIndex];
        switch (ch) {
            case '[' -> list.add(argumentValue(parseRecursive(globalIndex + 1, SEGMENT_END, new LinearState()).asComponent()));
            case '<' -> list.add(argumentValue(plainModifierValue(globalIndex + 1, PLAIN_ARG_CHECKPOINT)));
            case ')' -> {
                return;
            }
            default -> list.add(unknownAsString
                    ? argumentValue(plainModifierValue(globalIndex + 1, UNK_ARG_CHECKPOINT))
                    : argumentValue(parseRecursive(globalIndex + 1, UNK_ARG_CHECKPOINT, new LinearState()).asComponent())
            );
        }
        globalIndex++;
        collectArguments(list, unknownAsString);
    }

    /**
     * Extracts plain text until encountering any specified delimiter.
     * Also sets the globalIndex.
     * @param from start index for extraction
     * @param until set of stopping characters
     * @return extracted substring (excludes delimiters)
     */
    private @NotNull String plainText(int from, @NotNull CharPredicate until) {
        globalIndex = from;
        if (iterateUntil(until)) {
            return unescape(textArr, from, globalIndex);
        }
        return "";
    }

    /**
     * Extracts modifier value as plain text until closing parenthesis.
     * @param from start index for extraction
     * @return extracted value string
     */
    private @NotNull String plainModifierValue(int from, @NotNull CharPredicate until) {
        if (globalIndex == textLength) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        globalIndex = from;
        for (int lastIndex = globalIndex; globalIndex < textLength; globalIndex++) {
            char ch = textArr[globalIndex];
            if (until.test(ch)) {
                if (isUnescapedAt(textArr, globalIndex)) {
                    return builder.append(textArr, lastIndex, globalIndex - lastIndex).toString();
                }
            } else if (isSpecial(ch) && isUnescapedAt(textArr, globalIndex)) {
                int initIndex = globalIndex;
                PlaceholderData data = parsePlaceholder(context);
                if (data != null) {
                    builder.append(textArr, lastIndex, initIndex - lastIndex);
                    builder.append(data.placeholder.retrievePlain(data.params, context));
                    lastIndex = globalIndex + 1;
                }
            }
        }
        return "";
    }

    private static final CharPredicate PH_CHECKPOINT = CharPredicate.anyOf(":}");
    private @Nullable PlaceholderData parsePlaceholder(PlaceholderFinder placeholders) {
        int initIndex = globalIndex;
        if (!iterateUntil(PH_CHECKPOINT)) {
            globalIndex = initIndex;
            return null;
        }
        Placeholder placeholder = placeholders.findPlaceholder(unescape(textArr, initIndex + 2, globalIndex));
        if (placeholder == null) {
            globalIndex = initIndex;
            return null;
        }
        String params;
        if (textArr[globalIndex] == '}') {
            params = "";
        } else {
            int paramsIndex = (++globalIndex);
            if (!iterateUntil('}')) {
                globalIndex = initIndex;
                return null;
            }
            params = unescape(textArr, paramsIndex, globalIndex);
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
        for (
                int index = indexOf(textArr, until, globalIndex);
                index >= 0;
                index = indexOf(textArr, until, index + 1)
        ) {
            if (isUnescapedAt(textArr, index)) {
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
    private boolean iterateUntil(@NotNull CharPredicate until) {
        for (; globalIndex < textLength; globalIndex++) {
            if (until.test(textArr[globalIndex]) && isUnescapedAt(textArr, globalIndex)) return true;
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
