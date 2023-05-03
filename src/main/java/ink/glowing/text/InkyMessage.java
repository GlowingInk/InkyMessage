package ink.glowing.text;

import ink.glowing.text.placeholders.Placeholder;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.style.symbolic.SymbolicStyle;
import ink.glowing.text.style.tag.StyleTag;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return deserialize(inputText, standardResolver());
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param resolver resolver to use
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText, @NotNull Resolver resolver) {
        return deserialize(inputText, new BuildContext(resolver));
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param context context to deserialize with
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText, @NotNull BuildContext context) {
        return InkyParser.parse(inputText, context).compact();
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
        return InkyStringifier.stringify(text, resolver);
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
     * Using standard style tags, replacers, and notchian symbolic styles.
     * @return a standard resolver
     */
    public static @NotNull InkyMessage.Resolver standardResolver() {
        return InkyResolverImpl.STANDARD_RESOLVER;
    }

    public sealed interface Resolver permits InkyResolverImpl {
        @Nullable StyleTag<?> getTag(@NotNull String namespace);
        
        @Nullable Placeholder getPlaceholder(@NotNull String namespace);

        @Nullable Style applySymbolicStyle(char symbol, @NotNull Style currentStyle);

        @NotNull TreeSet<Replacer.FoundSpot> findReplacements(@NotNull String input);

        @NotNull TreeSet<SymbolicStyle> readSymbolics(@NotNull Component text);

        @NotNull List<String> readStyleTags(@NotNull Component text);

        @NotNull SymbolicStyle symbolicReset();

        @NotNull InkyMessage.ResolverBuilder toBuilder();
    }

    public static class ResolverBuilder implements AbstractBuilder<Resolver> {
        private Set<StyleTag<?>> tags;
        private Set<Replacer> replacers;
        private Set<SymbolicStyle> symbolics;
        private Set<Placeholder> placeholders;
        private SymbolicStyle symbolicReset;

        ResolverBuilder() {
            this.tags = new HashSet<>();
            this.replacers = new HashSet<>();
            this.symbolics = new HashSet<>();
            this.placeholders = new HashSet<>();
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder replacers(@NotNull Replacer @NotNull ... replacers) {
            return replacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder replacers(@NotNull Collection<@NotNull Replacer> replacers) {
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
        public @NotNull InkyMessage.ResolverBuilder addReplacers(@NotNull Iterable<@NotNull Replacer> replacers) {
            for (var replacer : replacers) addReplacer(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder placeholders(@NotNull Placeholder @NotNull ... placeholders) {
            return placeholders(Arrays.asList(placeholders));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder placeholders(@NotNull Collection<@NotNull Placeholder> placeholders) {
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
        public @NotNull InkyMessage.ResolverBuilder addPlaceholders(@NotNull Iterable<@NotNull Placeholder> placeholders) {
            for (var placeholder : placeholders) addPlaceholder(placeholder);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder tags(@NotNull StyleTag<?> @NotNull ... styleTags) {
            return tags(Arrays.asList(styleTags));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder tags(@NotNull Collection<@NotNull StyleTag<?>> styleTags) {
            this.tags = new HashSet<>(styleTags);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addTag(@NotNull StyleTag<?> tag) {
            this.tags.add(tag);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addTags(@NotNull StyleTag<?> @NotNull ... tags) {
            return addTags(Arrays.asList(tags));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder addTags(@NotNull Iterable<@NotNull StyleTag<?>> tags) {
            for (var tag : tags) addTag(tag);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder symbolicReset(@NotNull SymbolicStyle symbolicReset) {
            symbolics.remove(this.symbolicReset);
            symbolics.add(symbolicReset);
            this.symbolicReset = symbolicReset;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder symbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return symbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.ResolverBuilder symbolics(@NotNull Collection<@NotNull SymbolicStyle> symbolics) {
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
        public @NotNull InkyMessage.ResolverBuilder addSymbolics(@NotNull Iterable<@NotNull SymbolicStyle> symbolics) {
            for (var symbolic : symbolics) addSymbolic(symbolic);
            return this;
        }

        @Override
        @Contract("-> new")
        public @NotNull InkyMessage.Resolver build() {
            Objects.requireNonNull(symbolicReset, "Resolver requires symbolic reset to be provided");
            return new InkyResolverImpl(tags, placeholders, replacers, symbolics, symbolicReset);
        }
    }
}
