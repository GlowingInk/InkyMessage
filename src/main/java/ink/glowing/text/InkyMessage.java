package ink.glowing.text;

import ink.glowing.text.replace.Replacer;
import ink.glowing.text.style.symbolic.SymbolicStyle;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.function.InstanceProvider;
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

import static ink.glowing.text.InkyMessage.Resolver.standardResolver;

public final class InkyMessage implements ComponentSerializer<Component, Component, String> {
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[&\\]()]");
    private static final Pattern UNESCAPE_PATTERN = Pattern.compile("\\\\([&\\]()\\\\])");

    public static @NotNull InkyMessage inkyMessage() {
        return Provider.PROVIDER.instance;
    }

    private InkyMessage() {}

    /**
     * Convert string into adventure text component using standard resolver
     * @param inputText input string
     * @return converted text component
     * @see Resolver#standardResolver()
     */
    @Override
    public @NotNull Component deserialize(@NotNull String inputText) {
        return deserialize(inputText, standardResolver());
    }

    /**
     * Convert string into adventure text component
     * @param inputText input string
     * @param resolver resolver to use
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText, @NotNull Resolver resolver) {
        return deserialize(inputText, new BuildContext(resolver));
    }

    /**
     * Convert string into adventure text component
     * @param inputText input string
     * @param context context to build with
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText, @NotNull BuildContext context) {
        return IMDeserializerImpl.parse(inputText, context);
    }

    @Override
    public @NotNull String serialize(@NotNull Component text) {
        return serialize(text, standardResolver());
    }

    public @NotNull String serialize(@NotNull Component text, @NotNull Resolver resolver) {
        return IMSerializerImpl.serialize(text, resolver);
    }

    public static @NotNull String escape(@NotNull String text) {
        StringBuilder builder = new StringBuilder(text.length());
        Matcher matcher = ESCAPE_PATTERN.matcher(text);
        while (matcher.find()) {
            matcher.appendReplacement(builder, "");
            builder.append('\\').append(matcher.group());
        }
        return matcher.appendTail(builder).toString();
    }

    public static @NotNull String unescape(@NotNull String text) {
        StringBuilder builder = new StringBuilder(text.length());
        Matcher matcher = UNESCAPE_PATTERN.matcher(text);
        while (matcher.find()) {
            matcher.appendReplacement(builder, "");
            builder.append(matcher.group(1));
        }
        return matcher.appendTail(builder).toString();
    }

    public static boolean isEscapedAt(@NotNull String input, int index) {
        boolean escaped = false;
        while (--index > -1 && input.charAt(index) == '\\') escaped = !escaped;
        return escaped;
    }

    private enum Provider implements InstanceProvider<InkyMessage> {
        PROVIDER;
        private final InkyMessage instance = new InkyMessage();

        @Override
        public @NotNull InkyMessage get() {
            return instance;
        }
    }

    public interface Resolver {
        /**
         * Contains recommended options for a resolver
         * Using standard style tags, replacers, and Notchian symbolic styles
         *
         * @return a standard resolver
         */
        static @NotNull InkyMessage.Resolver standardResolver() {
            return IMResolverImpl.STANDARD_RESOLVER;
        }

        /**
         * Creates a new resolver builder
         *
         * @return a builder
         */
        static @NotNull InkyMessage.ResolverBuilder resolver() {
            return new ResolverBuilder();
        }

        @Nullable StyleTag<?> getTag(@NotNull String namespace);

        @Nullable Style applySymbolicStyle(char symbol, @NotNull Style currentStyle);

        @NotNull TreeSet<Replacer.FoundSpot> findReplacements(@NotNull String input);

        @NotNull TreeSet<SymbolicStyle> readSymbolics(@NotNull Component text);

        @NotNull List<String> readStyleTags(@NotNull Component text);

        @NotNull SymbolicStyle symbolicReset();

        @NotNull InkyMessage.ResolverBuilder toBuilder();
    }

    static class ResolverBuilder implements AbstractBuilder<Resolver> {
        private Set<StyleTag<?>> tags;
        private Set<Replacer> replacers;
        private Set<SymbolicStyle> symbolics;
        private SymbolicStyle symbolicReset;

        ResolverBuilder() {
            this.tags = new HashSet<>();
            this.replacers = new HashSet<>();
            this.symbolics = new HashSet<>();
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
            Objects.requireNonNull(symbolicReset, "InkyMessageResolver requires symbolic reset to be provided");
            return new IMResolverImpl(tags, replacers, symbolics, symbolicReset);
        }
    }
}
