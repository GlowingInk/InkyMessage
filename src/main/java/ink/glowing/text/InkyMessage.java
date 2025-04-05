package ink.glowing.text;

import ink.glowing.text.placeholders.Placeholder;
import ink.glowing.text.placeholders.PlaceholderGetter;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.style.modifier.ModifierGetter;
import ink.glowing.text.style.modifier.StyleModifier;
import ink.glowing.text.style.symbolic.StandardSymbolicStyles;
import ink.glowing.text.style.symbolic.SymbolicStyle;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ink.glowing.text.placeholders.PlaceholderGetter.placeholderGetter;

/**
 * User-friendly component (de)serializer with legacy format.
 */
public final class InkyMessage implements ComponentSerializer<Component, Component, String> {
    private static final String ESCAPABLE_SYMBOLS = "&]()}\\";
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[&\\]()}\\\\]");
    private static final Pattern UNESCAPE_PATTERN = Pattern.compile("\\\\([&\\]()\\\\}])");

    private static final InkyMessage INSTANCE = new InkyMessage();

    /**
     * Gets the instance of InkyMessage.
     * @return the instance
     */
    public static @NotNull InkyMessage inkyMessage() {
        return INSTANCE;
    }

    private InkyMessage() {}

    /**
     * Convert string into adventure text component using standard resolver.
     * @param inputText input string
     * @return converted text component
     * @see InkyMessage#standardResolver()
     */
    @Override
    public @NotNull Component deserialize(@NotNull String inputText) {
        return deserialize(inputText, new BuildContext(standardResolver()));
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param resolver resolver to use
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Resolver resolver) {
        return deserialize(inputText, new BuildContext(resolver));
    }

    /**
     * Convert string into adventure text component using standard resolver.
     * @param inputText input string
     * @param placeholders custom placeholders
     * @return converted text component
     * @see InkyMessage#standardResolver()
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Placeholder@NotNull ... placeholders) {
        return deserialize(inputText, new BuildContext(standardResolver(), placeholderGetter(placeholders)));
    }

    /**
     * Convert string into adventure text component using standard resolver.
     * @param inputText input string
     * @param placeholders custom placeholders
     * @return converted text component
     * @see InkyMessage#standardResolver()
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull PlaceholderGetter placeholders) {
        return deserialize(inputText, new BuildContext(standardResolver(), placeholders));
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param resolver resolver to use
     * @param placeholders custom placeholders
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Resolver resolver,
                                          @NotNull Placeholder@NotNull ... placeholders) {
        return deserialize(inputText, new BuildContext(resolver, placeholderGetter(placeholders)));
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param resolver resolver to use
     * @param placeholders custom placeholders
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Resolver resolver,
                                          @NotNull PlaceholderGetter placeholders) {
        return deserialize(inputText, new BuildContext(resolver, placeholders));
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param context context to deserialize with
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull BuildContext context) {
        return Parser.parse(inputText, context).compact();
    }

    /**
     * Convert adventure component into string using standard resolver.
     * @param text input component
     * @return converted string representation
     * @see InkyMessage#standardResolver()
     */
    @Override
    public @NotNull String serialize(@NotNull Component text) {
        return serialize(text, standardResolver());
    }

    /**
     * Convert adventure component into string.
     * @param text input component
     * @param resolver resolver to use
     * @return converted string representation
     * @see InkyMessage#standardResolver()
     */
    public @NotNull String serialize(@NotNull Component text, @NotNull Resolver resolver) {
        return Stringifier.stringify(text, resolver);
    }

    /**
     * Escape special characters with slashes.
     * @param text text to escape
     * @return escaped string
     */
    public static @NotNull String escape(@NotNull String text) {
        StringBuilder builder = new StringBuilder(text.length());
        Matcher matcher = ESCAPE_PATTERN.matcher(text);
        while (matcher.find()) {
            matcher.appendReplacement(builder, "");
            builder.append('\\').append(matcher.group());
        }
        return matcher.appendTail(builder).toString();
    }

    /**
     * Unescape special characters.
     * @param text text to unescape
     * @return unescaped string
     */
    public static @NotNull String unescape(@NotNull String text) {
        if (text.indexOf('\\') == -1) return text;
        StringBuilder builder = new StringBuilder(text.length());
        Matcher matcher = UNESCAPE_PATTERN.matcher(text);
        while (matcher.find()) {
            matcher.appendReplacement(builder, "");
            builder.append(matcher.group(1));
        }
        return matcher.appendTail(builder).toString();
    }

    /**
     * Check if character is escaped.
     * @param input text to check in
     * @param index index of character to check
     * @return is character escaped
     */
    public static boolean isEscapedAt(@NotNull String input, int index) {
        boolean escaped = false;
        while (--index > -1 && input.charAt(index) == '\\') escaped = !escaped;
        return escaped;
    }

    /**
     * Check if character is not escaped.
     * @param input text to check in
     * @param index index of character to check
     * @return is character unescaped
     */
    public static boolean isUnescapedAt(@NotNull String input, int index) {
        return !isEscapedAt(input, index);
    }

    /**
     * Check if character can be escaped.
     * @param ch character to check
     * @return is character escapable
     */
    public static boolean isEscapable(char ch) {
        return ESCAPABLE_SYMBOLS.indexOf(ch) != -1;
    }

    /**
     * Creates a new resolver builder.
     * @return a builder
     */
    public static @NotNull InkyMessage.ResolverBuilder resolver() {
        return new ResolverBuilder();
    }

    /**
     * Contains recommended options for a resolver.
     * Using standard style modifiers, replacers, and notchian symbolic styles.
     * @return a standard resolver
     */
    public static @NotNull InkyMessage.Resolver standardResolver() {
        return ink.glowing.text.Resolver.STANDARD_RESOLVER;
    }

    public sealed interface Resolver extends ModifierGetter, PlaceholderGetter permits ink.glowing.text.Resolver {
        @Nullable Style applySymbolicStyle(char symbol, @NotNull Style currentStyle);

        @NotNull TreeSet<Replacer.FoundSpot> matchReplacements(@NotNull String input);

        @NotNull TreeSet<SymbolicStyle> readSymbolics(@NotNull Component text);

        @NotNull List<String> readStyleModifiers(@NotNull Component text);

        @NotNull SymbolicStyle symbolicReset();

        @NotNull InkyMessage.ResolverBuilder toBuilder();
    }

    public static class ResolverBuilder implements AbstractBuilder<Resolver> {
        private Set<StyleModifier<?>> modifiers;
        private Set<Replacer> replacers;
        private Set<SymbolicStyle> symbolics;
        private Set<Placeholder> placeholders;
        private SymbolicStyle symbolicReset;

        ResolverBuilder() {
            this.modifiers = new HashSet<>();
            this.replacers = new HashSet<>();
            this.symbolics = new HashSet<>();
            this.placeholders = new HashSet<>();
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder replacers(@NotNull Replacer @NotNull ... replacers) {
            return replacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder replacers(@NotNull Collection<? extends @NotNull Replacer> replacers) {
            this.replacers = new HashSet<>(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addReplacer(@NotNull Replacer replacer) {
            this.replacers.add(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addReplacers(@NotNull Replacer @NotNull ... replacers) {
            return addReplacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addReplacers(@NotNull Collection<? extends @NotNull Replacer> replacers) {
            this.replacers.addAll(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addReplacers(@NotNull Iterable<? extends @NotNull Replacer> replacers) {
            for (var replacer : replacers) addReplacer(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder placeholders(@NotNull Placeholder @NotNull ... placeholders) {
            return placeholders(Arrays.asList(placeholders));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder placeholders(@NotNull Collection<? extends @NotNull Placeholder> placeholders) {
            this.placeholders = new HashSet<>(placeholders);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addPlaceholder(@NotNull Placeholder placeholder) {
            this.placeholders.add(placeholder);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addPlaceholders(@NotNull Placeholder @NotNull ... placeholders) {
            return addPlaceholders(Arrays.asList(placeholders));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addPlaceholders(@NotNull Collection<? extends @NotNull Placeholder> placeholders) {
            this.placeholders.addAll(placeholders);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addPlaceholders(@NotNull Iterable<? extends @NotNull Placeholder> placeholders) {
            for (var placeholder : placeholders) addPlaceholder(placeholder);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder modifiers(@NotNull StyleModifier<?> @NotNull ... styleModifiers) {
            return modifiers(Arrays.asList(styleModifiers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder modifiers(@NotNull Collection<? extends @NotNull StyleModifier<?>> styleModifiers) {
            this.modifiers = new HashSet<>(styleModifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addModifier(@NotNull StyleModifier<?> modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addModifiers(@NotNull StyleModifier<?> @NotNull ... modifiers) {
            return addModifiers(Arrays.asList(modifiers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addModifiers(@NotNull Collection<? extends @NotNull StyleModifier<?>> modifiers) {
            this.modifiers.addAll(modifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addModifiers(@NotNull Iterable<? extends @NotNull StyleModifier<?>> modifiers) {
            for (var modifier : modifiers) this.modifiers.add(modifier);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder symbolicReset(char symbol) {
            this.symbolicReset = StandardSymbolicStyles.simpleReset(symbol);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder symbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return symbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder symbolics(@NotNull Collection<? extends @NotNull SymbolicStyle> symbolics) {
            this.symbolics = new HashSet<>(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addSymbolic(@NotNull SymbolicStyle symbolics) {
            this.symbolics.add(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addSymbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return addSymbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addSymbolics(@NotNull Collection<? extends @NotNull SymbolicStyle> symbolics) {
            this.symbolics.addAll(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addSymbolics(@NotNull Iterable<? extends @NotNull SymbolicStyle> symbolics) {
            for (var symbolic : symbolics) addSymbolic(symbolic);
            return this;
        }

        @Override
        @Contract("-> new")
        public @NotNull InkyMessage.Resolver build() {
            Objects.requireNonNull(symbolicReset, "Resolver requires symbolic reset to be provided");

            symbolics.add(symbolicReset);
            var resolver = new ink.glowing.text.Resolver(
                    modifiers, placeholders, replacers, symbolics, symbolicReset
            );
            symbolics.remove(symbolicReset);

            return resolver;
        }
    }
}
