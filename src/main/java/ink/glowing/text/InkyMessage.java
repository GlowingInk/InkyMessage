package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierGetter;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.placeholder.PlaceholderGetter;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.symbolic.StandardSymbolicStyles;
import ink.glowing.text.symbolic.SymbolicStyle;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static ink.glowing.text.Stringifier.stringify;
import static ink.glowing.text.modifier.standard.StandardModifiers.standardModifiers;
import static ink.glowing.text.replace.StandardReplacers.urlReplacer;
import static ink.glowing.text.symbolic.StandardSymbolicStyles.notchianFormat;
import static ink.glowing.text.symbolic.StandardSymbolicStyles.notchianResetSymbol;

/**
 * User-friendly component (de)serializer with legacy-inspired format.
 */
public final class InkyMessage implements ComponentSerializer<Component, Component, String> {
    private static final InkyMessage.Resolver STANDARD_RESOLVER = resolverBuilder()
            .addModifiers(standardModifiers())
            .addSymbolics(notchianFormat())
            .symbolicReset(notchianResetSymbol())
            .addReplacer(urlReplacer())
            .build();

    private static final InkyMessage STANDARD_INSTANCE = new InkyMessage(STANDARD_RESOLVER);

    private final Resolver resolver;

    private InkyMessage(@NotNull Resolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Gets the standard instance of InkyMessage.
     * @return the instance
     * @see InkyMessage#standardResolver()
     */
    public static @NotNull InkyMessage inkyMessage() {
        return STANDARD_INSTANCE;
    }

    /**
     * Creates an instance of InkyMessage using provided resolver.
     * @param resolver resolver to use
     * @return an instance
     */
    public static @NotNull InkyMessage inkyMessage(@NotNull Resolver resolver) {
        return new InkyMessage(resolver);
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @return converted text component
     */
    @Override
    public @NotNull Component deserialize(@NotNull String inputText) {
        return deserialize(inputText, this.resolver);
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param ink additional style ink
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Ink ink) {
        return deserialize(inputText, this.resolver, ink);
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param inks additional style inks
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Ink @NotNull ... inks) {
        return deserialize(inputText, this.resolver, inks);
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param inks additional style inks
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Iterable<@NotNull Ink> inks) {
        return deserialize(inputText, this.resolver, inks);
    }

    /**
     * Convert string into adventure text component using provided resolver.
     * @param inputText input string
     * @param resolver resolver to use
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Resolver resolver) {
        return deserialize(inputText, new BuildContext(resolver));
    }

    /**
     * Convert string into adventure text component using provided resolver.
     * @param inputText input string
     * @param resolver resolver to use
     * @param ink additional style ink
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Resolver resolver,
                                          @NotNull Ink ink) {
        return deserialize(inputText, new BuildContext(resolver.toBuilder().addInk(ink).build()));
    }

    /**
     * Convert string into adventure text component using provided resolver.
     * @param inputText input string
     * @param resolver resolver to use
     * @param inks additional style inks
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Resolver resolver,
                                          @NotNull Ink @NotNull ... inks) {
        return deserialize(inputText, resolver, Arrays.asList(inks));
    }

    /**
     * Convert string into adventure text component using provided resolver.
     * @param inputText input string
     * @param resolver resolver to use
     * @param inks additional style inks
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Resolver resolver,
                                          @NotNull Iterable<@NotNull Ink> inks) {
        return deserialize(inputText, new BuildContext(resolver.toBuilder().addInks(inks).build()));
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param context context to deserialize with
     * @return converted text component
     */
    private @NotNull Component deserialize(@NotNull String inputText,
                                           @NotNull BuildContext context) {
        return Parser.parse(inputText, context).compact();
    }

    /**
     * Convert adventure component into string.
     * @param text input component
     * @return converted string representation
     */
    @Override
    public @NotNull String serialize(@NotNull Component text) {
        return stringify(text, this.resolver);
    }

    /**
     * Convert adventure component into string using provided resolver.
     * @param text input component
     * @param resolver resolver to use
     * @return converted string representation
     */
    public @NotNull String serialize(@NotNull Component text, @NotNull Resolver resolver) {
        return stringify(text, resolver);
    }

    /**
     * Escape special characters with slashes.
     * @param text text to escape
     * @return escaped string
     */
    public static @NotNull String escape(@NotNull String text) {
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
     * Unescape special characters.
     * @param text text to unescape
     * @return unescaped string
     */
    public static @NotNull String unescape(@NotNull String text) {
        if (text.indexOf('\\') == -1) return text;

        StringBuilder builder = new StringBuilder(text.length());
        int length = text.length();

        for (
                int index = 0, nextIndex = text.indexOf('\\');
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
     * Check if character is special and should be escaped.
     * @param ch character to check
     * @return is character escapable
     */
    public static boolean isSpecial(char ch) {
        return switch (ch) {
            case '&', '[', ']', '(', ')', '}', '\\' -> true;
            default -> false;
        };
    }

    /**
     * Check if character is not special.
     * @param ch character to check
     * @return is character unescapable
     */
    public static boolean isNotSpecial(char ch) {
        return !isSpecial(ch);
    }

    /**
     * Get currently used resolver.
     * @return current resolver
     */
    public @NotNull Resolver resolver() {
        return resolver;
    }

    /**
     * Creates a new resolver builder.
     * @return a builder
     */
    public static @NotNull ResolverBuilder resolverBuilder() {
        return new ResolverBuilder();
    }

    /**
     * Contains recommended options for a resolver.
     * Using standard modifiers, replacers, and notchian symbolic styles.
     * @return a standard resolver
     */
    public static @NotNull InkyMessage.Resolver standardResolver() {
        return STANDARD_RESOLVER;
    }

    // TODO Merge to InkyMessage
    public sealed interface Resolver extends ModifierGetter, PlaceholderGetter permits ink.glowing.text.Resolver {

        /**
         * Applies symbolic style to the provided one.
         * @param symbol style symbol
         * @param currentStyle style to be applied onto
         * @return provided style with applied symbolic style, or null if no styles were found with such symbol
         */
        @Nullable Style applySymbolicStyle(char symbol, @NotNull Style currentStyle);

        /**
         * Find replaceable spots in a string.
         * @param input string to replace in
         * @return found spots
         */
        @NotNull TreeSet<Replacer.FoundSpot> matchReplacements(@NotNull String input);

        @NotNull TreeSet<SymbolicStyle> readSymbolics(@NotNull Style style);

        @NotNull List<String> readStyleModifiers(@NotNull Component text);

        @NotNull SymbolicStyle symbolicReset();

        @NotNull ResolverBuilder toBuilder();
    }

    public static class ResolverBuilder implements AbstractBuilder<Resolver> {
        private Set<Modifier<?>> modifiers;
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
        
        public @NotNull ResolverBuilder addInk(@NotNull Ink ink) {
            return switch (ink) {
                case Placeholder ph -> addPlaceholder(ph);
                case Modifier<?> mod -> addModifier(mod);
                case Replacer rp -> addReplacer(rp);
                case SymbolicStyle sym -> addSymbolic(sym);
                default -> throw new IllegalArgumentException("Unknown ink type: " + ink.getClass().getSimpleName());
            };
        }

        public @NotNull ResolverBuilder addInks(@NotNull Ink @NotNull ... inks) {
            for (var ink : inks) addInk(ink);
            return this;
        }

        public @NotNull ResolverBuilder addInks(@NotNull Iterable<@NotNull Ink> inks) {
            for (var ink : inks) addInk(ink);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder replacers(@NotNull Replacer @NotNull ... replacers) {
            return replacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder replacers(@NotNull Collection<? extends @NotNull Replacer> replacers) {
            this.replacers = new HashSet<>(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addReplacer(@NotNull Replacer replacer) {
            this.replacers.add(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addReplacers(@NotNull Replacer @NotNull ... replacers) {
            return addReplacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addReplacers(@NotNull Collection<? extends @NotNull Replacer> replacers) {
            this.replacers.addAll(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addReplacers(@NotNull Iterable<? extends @NotNull Replacer> replacers) {
            for (var replacer : replacers) addReplacer(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder placeholders(@NotNull Placeholder @NotNull ... placeholders) {
            return placeholders(Arrays.asList(placeholders));
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder placeholders(@NotNull Collection<? extends @NotNull Placeholder> placeholders) {
            this.placeholders = new HashSet<>(placeholders);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addPlaceholder(@NotNull Placeholder placeholder) {
            this.placeholders.add(placeholder);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addPlaceholders(@NotNull Placeholder @NotNull ... placeholders) {
            return addPlaceholders(Arrays.asList(placeholders));
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addPlaceholders(@NotNull Collection<? extends @NotNull Placeholder> placeholders) {
            this.placeholders.addAll(placeholders);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addPlaceholders(@NotNull Iterable<? extends @NotNull Placeholder> placeholders) {
            for (var placeholder : placeholders) addPlaceholder(placeholder);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder modifiers(@NotNull Modifier<?> @NotNull ... modifiers) {
            return modifiers(Arrays.asList(modifiers));
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder modifiers(@NotNull Collection<? extends @NotNull Modifier<?>> styleModifiers) {
            this.modifiers = new HashSet<>(styleModifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addModifier(@NotNull Modifier<?> modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addModifiers(@NotNull Modifier<?> @NotNull ... modifiers) {
            return addModifiers(Arrays.asList(modifiers));
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addModifiers(@NotNull Collection<? extends @NotNull Modifier<?>> modifiers) {
            this.modifiers.addAll(modifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addModifiers(@NotNull Iterable<? extends @NotNull Modifier<?>> modifiers) {
            for (var modifier : modifiers) this.modifiers.add(modifier);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder symbolicReset(char symbol) {
            this.symbolicReset = StandardSymbolicStyles.simpleReset(symbol);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder symbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return symbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder symbolics(@NotNull Collection<? extends @NotNull SymbolicStyle> symbolics) {
            this.symbolics = new HashSet<>(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addSymbolic(@NotNull SymbolicStyle symbolics) {
            this.symbolics.add(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addSymbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return addSymbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addSymbolics(@NotNull Collection<? extends @NotNull SymbolicStyle> symbolics) {
            this.symbolics.addAll(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull ResolverBuilder addSymbolics(@NotNull Iterable<? extends @NotNull SymbolicStyle> symbolics) {
            for (var symbolic : symbolics) addSymbolic(symbolic);
            return this;
        }

        @Override
        @Contract("-> new")
        public @NotNull InkyMessage.Resolver build() {
            return new ink.glowing.text.Resolver(
                    modifiers, placeholders, replacers, symbolics,
                    Objects.requireNonNull(symbolicReset, "Resolver requires symbolic reset to be provided")
            );
        }
    }
}
