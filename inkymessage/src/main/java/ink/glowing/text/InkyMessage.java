package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierFinder;
import ink.glowing.text.modifier.standard.StandardModifiers;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.placeholder.PlaceholderFinder;
import ink.glowing.text.placeholder.StandardPlaceholders;
import ink.glowing.text.replace.ReplacementMatcher;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.replace.StandardReplacers;
import ink.glowing.text.symbolic.SymbolicStyle;
import ink.glowing.text.symbolic.SymbolicStyleFinder;
import ink.glowing.text.symbolic.standard.StandardSymbolicStyles;
import ink.glowing.text.utils.processor.DecodeProcessors;
import ink.glowing.text.utils.processor.EncodeProcessors;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

import static ink.glowing.text.symbolic.standard.StandardSymbolicStyles.simpleReset;
import static ink.glowing.text.utils.GeneralUtils.indexOf;
import static ink.glowing.text.utils.processor.DecodeProcessors.identityDecodePreProcessors;
import static ink.glowing.text.utils.processor.EncodeProcessors.identityEncodePreprocessors;

/**
 * User-friendly component (de)serializer with legacy-inspired format.
 */
@ApiStatus.NonExtendable
public sealed interface InkyMessage extends ComponentSerializer<Component, Component, String>,
        ModifierFinder, PlaceholderFinder, SymbolicStyleFinder, ReplacementMatcher permits InkyMessageImpl {
    /**
     * The standard instance of InkyMessage.
     * @return the standard instance
     * @see StandardModifiers#standardModifiers()
     * @see StandardPlaceholders#requiredPlaceholders()
     * @see StandardPlaceholders#standardPlaceholders()
     * @see StandardSymbolicStyles#notchianFormat()
     * @see StandardSymbolicStyles#standardResetSymbol()
     * @see StandardReplacers#urlReplacer()
     */
    @Contract(pure = true)
    static @NotNull InkyMessage inkyMessage() {
        return InkyMessageImpl.STANDARD;
    }

    /**
     * Creates new instance of InkyMessage with the provided style ink.
     * @param ink style ink to use
     * @return the new instance
     * @see InkyMessage#builder()
     */
    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull InkyMessage inkyMessage(char reset, @NotNull Ink ink) {
        return builder().addInk(ink).symbolicReset(reset).build();
    }

    /**
     * Creates new instance of InkyMessage with the provided style ink.
     * @param inks style inks to use
     * @return the new instance
     * @see InkyMessage#builder()
     */
    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull InkyMessage inkyMessage(char reset, @NotNull Ink @NotNull ... inks) {
        return builder().addInks(inks).symbolicReset(reset).build();
    }

    /**
     * Creates new instance of InkyMessage with the provided style ink.
     * @param inks style inks to use
     * @return the new instance
     * @see InkyMessage#builder()
     */
    @Contract(value = "_, _ -> new", pure = true)
    static @NotNull InkyMessage inkyMessage(char reset, @NotNull Iterable<? extends @NotNull Ink> inks) {
        return builder().addInks(inks).symbolicReset(reset).build();
    }

    /**
     * Escapes special characters with slashes.
     * @param text text to escape
     * @return escaped string
     */
    @Contract(pure = true)
    static @NotNull String escape(@NotNull String text) {
        StringBuilder builder = new StringBuilder(text.length());
        for (int index = 0; index < text.length(); index++) {
            char ch = text.charAt(index);
            if (isSpecial(ch)) {
                builder.append('\\');
            }
            builder.append(ch);
        }
        return builder.toString();
    }

    /**
     * Escapes special characters with slashes.
     * @param textArray character array containing text to escape
     * @param from starting index (inclusive)
     * @param to ending index (exclusive)
     * @return escaped string
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull String escape(char[] textArray, int from, int to) {
        StringBuilder builder = new StringBuilder(to - from);
        for (int index = from; index < to; index++) {
            char ch = textArray[index];
            if (isSpecial(ch)) {
                builder.append('\\');
            }
            builder.append(ch);
        }
        return builder.toString();
    }

    /**
     * Unescapes special characters.
     * @param text text to unescape
     * @return unescaped string
     */
    @Contract(pure = true)
    static @NotNull String unescape(@NotNull String text) {
        int nextIndex = text.indexOf('\\');
        if (nextIndex == -1) return text;
        final int length = text.length();
        StringBuilder builder = new StringBuilder(length);
        for (
                int index = 0;
                index < length;
                index = nextIndex + 2, nextIndex = text.indexOf('\\', index)
        ) {
            if (nextIndex == -1 || nextIndex + 1 >= length) {
                builder.append(text, index, length);
                break;
            }
            builder.append(text, index, nextIndex);
            char nextCh = text.charAt(nextIndex + 1);
            if (isNotSpecial(nextCh)) builder.append('\\');
            builder.append(nextCh);
        }

        return builder.toString();
    }

    /**
     * Unescapes special characters.
     * @param textArray character array containing text to unescape
     * @param from starting index (inclusive)
     * @param to ending index (exclusive)
     * @return unescaped string
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull String unescape(char @NotNull [] textArray, int from, int to) {
        int nextIndex = indexOf(textArray, '\\', from, to);
        if (nextIndex == -1) return new String(textArray, from, to - from);
        StringBuilder builder = new StringBuilder(to - from);
        for (
                int index = from;
                index < to;
                index = nextIndex + 2, nextIndex = indexOf(textArray, '\\', index, to)
        ) {
            if (nextIndex == -1 || nextIndex + 1 >= to) {
                builder.append(textArray, index, to - index);
                break;
            }
            builder.append(textArray, index, nextIndex - index);
            char nextCh = textArray[nextIndex + 1];
            if (isNotSpecial(nextCh)) builder.append('\\');
            builder.append(nextCh);
        }
        return builder.toString();
    }

    /**
     * Checks if character is escaped.
     * @param input text to check in
     * @param index index of character to check
     * @return is character escaped
     */
    @Contract(pure = true)
    static boolean isEscapedAt(@NotNull String input, int index) {
        boolean escaped = false;
        while (--index > -1 && input.charAt(index) == '\\') escaped = !escaped;
        return escaped;
    }

    /**
     * Checks if character is escaped.
     * @param textArray character array containing text to check in
     * @param index index of character to check
     * @return is character escaped
     */
    @Contract(pure = true)
    static boolean isEscapedAt(char @NotNull [] textArray, int index) {
        return isEscapedAt(textArray, index, 0);
    }

    /**
     * Checks if character is escaped.
     * @param textArray character array containing text to check in
     * @param index index of character to check
     * @param from starting index of the text (inclusive)
     * @return is character escaped
     */
    @Contract(pure = true)
    static boolean isEscapedAt(char @NotNull [] textArray, int index, int from) {
        boolean escaped = false;
        while (--index >= from && textArray[index] == '\\') escaped = !escaped;
        return escaped;
    }

    /**
     * Checks if character is not escaped.
     * @param input text to check in
     * @param index index of character to check
     * @return is character unescaped
     */
    @Contract(pure = true)
    static boolean isUnescapedAt(@NotNull String input, int index) {
        return !isEscapedAt(input, index);
    }

    /**
     * Checks if character is not escaped.
     * @param textArray character array containing text to check in
     * @param index index of character to check
     * @return is character unescaped
     */
    @Contract(pure = true)
    static boolean isUnescapedAt(char[] textArray, int index) {
        return !isEscapedAt(textArray, index, 0);
    }

    /**
     * Checks if character is not escaped.
     * @param textArray character array containing text to check in
     * @param index index of character to check
     * @param from starting index of the text (inclusive)
     * @return is character unescaped
     */
    @Contract(pure = true)
    static boolean isUnescapedAt(char[] textArray, int index, int from) {
        return !isEscapedAt(textArray, index, from);
    }

    /**
     * Checks if character is special and should be escaped.
     * @param ch character to check
     * @return is character escapable
     */
    @Contract(pure = true)
    static boolean isSpecial(char ch) {
        return switch (ch) {
            case '&', '[', ']', '(', ')', '{', '}', '<', '>','\\' -> true;
            default -> false;
        };
    }

    /**
     * Checks if character is not special.
     * @param ch character to check
     * @return is character unescapable
     */
    @Contract(pure = true)
    static boolean isNotSpecial(char ch) {
        return !isSpecial(ch);
    }

    /**
     * Map of style modifiers that this InkyMessage instance uses.
     * @return map of style modifiers
     */
    @Contract(pure = true)
    @Unmodifiable @NotNull Map<String, Modifier> modifiers();

    /**
     * Map of placeholders that this InkyMessage instance uses.
     * @return map of placeholders
     */
    @Contract(pure = true)
    @Unmodifiable @NotNull Map<String, Placeholder> placeholders();

    /**
     * Map of symbolic styles that this InkyMessage instance uses.
     * @return map of symbolic styles
     */
    @Contract(pure = true)
    @Unmodifiable @NotNull Map<Character, SymbolicStyle> symbolics();

    /**
     * Collection of replacers that this InkyMessage instance uses.
     * @return collection of replacers
     */
    @Contract(pure = true)
    @Unmodifiable @NotNull Collection<Replacer> replacers();

    /**
     * Symbolic style reset that this InkyMessage instance uses.
     * @return symbolic reset
     */
    @Contract(pure = true)
    @NotNull SymbolicStyle symbolicReset();

    /**
     * Returns the base Context of this InkyMessage instance.
     * @return a new context
     */
    @Contract(value = "-> new", pure = true)
    @NotNull Context baseContext();

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @return converted text component
     */
    @Override
    @NotNull Component deserialize(@NotNull String inputText);

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param ink additional style ink
     * @return converted text component
     */
    @NotNull Component deserialize(@NotNull String inputText,
                                   @NotNull Ink ink);

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param inks additional style inks
     * @return converted text component
     */
    @NotNull Component deserialize(@NotNull String inputText,
                                   @NotNull Ink @NotNull ... inks);

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param inks additional style inks
     * @return converted text component
     */
    @NotNull Component deserialize(@NotNull String inputText,
                                   @NotNull Iterable<? extends @NotNull Ink> inks);

    /**
     * Convert adventure component into string.
     * @param text input component
     * @return converted string representation
     */
    @Override
    @NotNull String serialize(@NotNull Component text);

    /**
     * Convert adventure component into string.
     * @param text input component
     * @param ink additional style ink
     * @return converted string representation
     */
    default @NotNull String serialize(@NotNull Component text,
                                      @NotNull Ink ink) {
        return with(ink).serialize(text);
    }

    /**
     * Convert adventure component into string.
     * @param text input component
     * @param inks additional style inks
     * @return converted string representation
     */
    default @NotNull String serialize(@NotNull Component text,
                                      @NotNull Ink @NotNull ... inks) {
        return with(inks).serialize(text);
    }

    /**
     * Convert adventure component into string.
     * @param text input component
     * @param inks additional style inks
     * @return converted string representation
     */
    default @NotNull String serialize(@NotNull Component text,
                                      @NotNull Iterable<? extends @NotNull Ink> inks) {
        return with(inks).serialize(text);
    }

    @NotNull EncodeProcessors encodeProcessors();

    @NotNull DecodeProcessors decodeProcessors();
    
    @Contract(value = "_ -> new", pure = true)
    default @NotNull InkyMessage with(@NotNull Ink ink) {
        return toBuilder().addInk(ink).build();
    }

    @Contract(value = "_ -> new", pure = true)
    default @NotNull InkyMessage with(@NotNull Ink @NotNull ... inks) {
        return toBuilder().addInks(inks).build();
    }

    @Contract(value = "_ -> new", pure = true)
    default @NotNull InkyMessage with(@NotNull Iterable<? extends @NotNull Ink> inks) {
        return toBuilder().addInks(inks).build();
    }

    /**
     * Creates a new InkyMessage builder using this instance's values.
     * @return a builder
     */
    @Contract(value = "-> new", pure = true)
    default @NotNull InkyMessage.Builder toBuilder() {
        return new Builder(
                new HashMap<>(modifiers()),
                new HashMap<>(placeholders()),
                new HashMap<>(symbolics()),
                new HashSet<>(replacers()),
                symbolicReset(),
                encodeProcessors(),
                decodeProcessors()
        );
    }

    /**
     * Creates a new empty InkyMessage builder.
     * @return a builder
     */
    @Contract(value = "-> new", pure = true)
    static @NotNull InkyMessage.Builder emptyBuilder() {
        return new Builder();
    }

    /**
     * Creates a new InkyMessage builder using standard {@link Ink}s.
     * @return a builder
     * @see InkyMessage#inkyMessage()
     */
    @Contract(value = "-> new", pure = true)
    static @NotNull InkyMessage.Builder builder() {
        return InkyMessageImpl.STANDARD.toBuilder();
    }

    class Builder implements AbstractBuilder<InkyMessage> {
        private Map<String, Modifier> modifiers;
        private Map<String, Placeholder> placeholders;
        private Map<Character, SymbolicStyle> symbolics;
        private Collection<Replacer> replacers;
        private SymbolicStyle symbolicReset;

        private EncodeProcessors encodeProcessors;
        private DecodeProcessors decodeProcessors;

        Builder() {
            this.modifiers = new HashMap<>();
            this.placeholders = new HashMap<>();
            this.symbolics = new HashMap<>();
            this.replacers = new HashSet<>();

            this.encodeProcessors = identityEncodePreprocessors();
            this.decodeProcessors = identityDecodePreProcessors();
        }

        Builder(
                Map<String, Modifier> modifiers,
                Map<String, Placeholder> placeholders,
                Map<Character, SymbolicStyle> symbolics,
                Collection<Replacer> replacers,
                SymbolicStyle symbolicReset,

                EncodeProcessors encodeProcessors,
                DecodeProcessors decodeProcessors
        ) {
            this.modifiers = modifiers;
            this.placeholders = placeholders;
            this.symbolics = symbolics;
            this.replacers = replacers;
            this.symbolicReset = symbolicReset;

            this.encodeProcessors = encodeProcessors;
            this.decodeProcessors = decodeProcessors;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addInk(@NotNull Ink ink) {
            return switch (ink) {
                case Placeholder ph -> addPlaceholder(ph);
                case Modifier mod -> addModifier(mod);
                case Replacer rp -> addReplacer(rp);
                case SymbolicStyle sym -> addSymbolic(sym);
                case Ink.Provider pr -> addInks(pr.inks());
                default -> throw new IllegalArgumentException("Unknown ink type: " + ink.getClass().getSimpleName());
            };
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addInks(@NotNull Ink @NotNull ... inks) {
            for (var ink : inks) addInk(ink);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addInks(@NotNull Iterable<? extends @NotNull Ink> inks) {
            for (var ink : inks) addInk(ink);
            return this;
        }

        @Contract("-> new")
        public @NotNull Collection<Replacer> replacers() {
            return new HashSet<>(replacers);
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder replacers(@NotNull Replacer @NotNull ... replacers) {
            return replacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder replacers(@NotNull Collection<? extends @NotNull Replacer> replacers) {
            this.replacers = new HashSet<>(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder replacers(@NotNull Iterable<? extends @NotNull Replacer> replacers) {
            this.replacers = new HashSet<>();
            return addReplacers(replacers);
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addReplacer(@NotNull Replacer replacer) {
            this.replacers.add(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addReplacers(@NotNull Replacer @NotNull ... replacers) {
            return addReplacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addReplacers(@NotNull Collection<? extends @NotNull Replacer> replacers) {
            this.replacers.addAll(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removeReplacer(@NotNull Replacer replacer) {
            this.replacers.remove(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removeReplacers(@NotNull Replacer @NotNull ... replacers) {
            return removeReplacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removeReplacers(@NotNull Collection<? extends @NotNull Replacer> replacers) {
            this.replacers.removeAll(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addReplacers(@NotNull Iterable<? extends @NotNull Replacer> replacers) {
            for (var replacer : replacers) addReplacer(replacer);
            return this;
        }

        @Contract("-> new")
        public @NotNull Collection<Placeholder> placeholders() {
            return new HashSet<>(placeholders.values());
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder placeholders(@NotNull Placeholder @NotNull ... placeholders) {
            return placeholders(Arrays.asList(placeholders));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder placeholders(@NotNull Collection<? extends @NotNull Placeholder> placeholders) {
            this.placeholders = new HashMap<>(placeholders.size());
            return addPlaceholders(placeholders);
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder placeholders(@NotNull Iterable<? extends @NotNull Placeholder> placeholders) {
            this.placeholders = new HashMap<>();
            return addPlaceholders(placeholders);
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder placeholders(@NotNull Map<String, Placeholder> placeholders) {
            this.placeholders = new HashMap<>(placeholders);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addPlaceholder(@NotNull Placeholder placeholder) {
            this.placeholders.put(placeholder.label(), placeholder);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addPlaceholders(@NotNull Placeholder @NotNull ... placeholders) {
            return addPlaceholders(Arrays.asList(placeholders));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addPlaceholders(@NotNull Iterable<? extends @NotNull Placeholder> placeholders) {
            for (var placeholder : placeholders) addPlaceholder(placeholder);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addPlaceholders(@NotNull Map<String, Placeholder> placeholders) {
            this.placeholders.putAll(placeholders);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removePlaceholder(@NotNull String placeholderLabel) {
            this.placeholders.remove(placeholderLabel);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removePlaceholders(@NotNull String @NotNull ... placeholderLabels) {
            return removePlaceholders(Arrays.asList(placeholderLabels));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removePlaceholders(@NotNull Iterable<? extends @NotNull String> placeholderLabels) {
            for (var placeholderLabel : placeholderLabels) removePlaceholder(placeholderLabel);
            return this;
        }

        @Contract("-> new")
        public @NotNull Collection<Modifier> modifiers() {
            return new HashSet<>(modifiers.values());
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder modifiers(@NotNull Modifier @NotNull ... modifiers) {
            return modifiers(Arrays.asList(modifiers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder modifiers(@NotNull Collection<? extends @NotNull Modifier> modifiers) {
            this.modifiers = new HashMap<>(modifiers.size());
            return addModifiers(modifiers);
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder modifiers(@NotNull Iterable<? extends @NotNull Modifier> modifiers) {
            this.modifiers = new HashMap<>();
            return addModifiers(modifiers);
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder modifiers(@NotNull Map<String, Modifier> modifiers) {
            this.modifiers = new HashMap<>(modifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addModifier(@NotNull Modifier modifier) {
            this.modifiers.put(modifier.label(), modifier);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addModifiers(@NotNull Modifier @NotNull ... modifiers) {
            return addModifiers(Arrays.asList(modifiers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addModifiers(@NotNull Iterable<? extends @NotNull Modifier> modifiers) {
            for (var modifier : modifiers) addModifier(modifier);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addModifiers(@NotNull Map<String, Modifier> modifiers) {
            this.modifiers.putAll(modifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removeModifier(@NotNull String modifierLabel) {
            this.modifiers.remove(modifierLabel);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removeModifiers(@NotNull String @NotNull ... modifierLabels) {
            return removeModifiers(Arrays.asList(modifierLabels));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removeModifiers(@NotNull Iterable<@NotNull String> modifierLabels) {
            for (var modifierLabel : modifierLabels) removeModifier(modifierLabel);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder symbolicReset(char symbol) {
            this.symbolicReset = simpleReset(symbol);
            return this;
        }

        @Contract("-> new")
        public @NotNull Collection<SymbolicStyle> symbolics() {
            return new HashSet<>(symbolics.values());
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder symbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return symbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder symbolics(@NotNull Collection<? extends @NotNull SymbolicStyle> symbolics) {
            this.symbolics = new HashMap<>(symbolics.size());
            return addSymbolics(symbolics);
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder symbolics(@NotNull Map<Character, SymbolicStyle> symbolics) {
            this.symbolics = new HashMap<>(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addSymbolic(@NotNull SymbolicStyle symbolic) {
            this.symbolics.put(symbolic.symbol(), symbolic);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addSymbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return addSymbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addSymbolics(@NotNull Iterable<? extends @NotNull SymbolicStyle> symbolics) {
            for (var symbolic : symbolics) addSymbolic(symbolic);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addSymbolics(@NotNull Map<Character, SymbolicStyle> symbolics) {
            this.symbolics.putAll(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removeSymbolic(@NotNull Character symbolicSymbol) {
            this.symbolics.remove(symbolicSymbol);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removeSymbolics(@NotNull Character @NotNull ... symbolicsSymbols) {
            return removeSymbolics(Arrays.asList(symbolicsSymbols));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removeSymbolics(@NotNull Iterable<? extends @NotNull Character> symbolicSymbols) {
            for (var symbolicSymbol : symbolicSymbols) removeSymbolic(symbolicSymbol);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder removeSymbolics(char @NotNull ... symbolicSymbols) {
            for (var symbolicSymbol : symbolicSymbols) removeSymbolic(symbolicSymbol);
            return this;
        }

        @Contract(pure = true)
        public @NotNull EncodeProcessors encodeProcessors() {
            return encodeProcessors;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder encodeProcessors(@NotNull EncodeProcessors encodeProcessors) {
            this.encodeProcessors = encodeProcessors;
            return this;
        }

        @Contract(pure = true)
        public @NotNull DecodeProcessors decodeProcessors() {
            return decodeProcessors;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder decodeProcessors(@NotNull DecodeProcessors decodeProcessors) {
            this.decodeProcessors = decodeProcessors;
            return this;
        }

        @Contract("-> new")
        @Override
        public @NotNull InkyMessage build() {
            return new InkyMessageImpl(
                    new HashMap<>(modifiers),
                    new HashMap<>(placeholders),
                    new HashMap<>(symbolics),
                    new HashSet<>(replacers),
                    Objects.requireNonNull(symbolicReset, "Resolver requires symbolic reset to be provided"), // TODO Reset should not be required for serialization
                    encodeProcessors,
                    decodeProcessors
            );
        }
    }
}
