package ink.glowing.text;

import ink.glowing.text.replace.Replacer;
import ink.glowing.text.rich.RichNode;
import ink.glowing.text.style.symbolic.SymbolicStyle;
import ink.glowing.text.style.symbolic.impl.VirtualHexSymbolicStyle;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.style.tag.standard.ClickTag;
import ink.glowing.text.style.tag.standard.ColorTag;
import ink.glowing.text.style.tag.standard.DecorTag;
import ink.glowing.text.style.tag.standard.FontTag;
import ink.glowing.text.style.tag.standard.GradientTag;
import ink.glowing.text.style.tag.standard.HoverTag;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ink.glowing.text.replace.StandardReplacers.urlReplacer;
import static ink.glowing.text.style.symbolic.SymbolicStyle.*;

public final class InkyMessageResolver {
    private static final Pattern TAGS_PATTERN = Pattern.compile("\\(([^:\\s]+)(?::(\\S+))?(?: ([^)]*))?\\)");

    private static final InkyMessageResolver STANDARD_RESOLVER = inkyResolver()
            .addTags(ColorTag.colorTag(),
                    HoverTag.hoverTag(),
                    ClickTag.clickTag(),
                    GradientTag.gradientTag(),
                    FontTag.fontTag(),
                    DecorTag.decorTag())
            .addSymbolics(notchianColors())
            .addSymbolics(notchianDecorations())
            .symbolicReset(notchianReset())
            .addReplacer(urlReplacer())
            .build();

    private final Map<String, StyleTag> tags;
    private final Collection<Replacer> replacers;
    private final Map<Character, SymbolicStyle> symbolics;
    private final SymbolicStyle symbolicReset;

    /**
     * Contains recommended options for a resolver
     * Using standard style tags, replacers, and Notchian symbolic styles
     * @return a standard resolver
     */
    public static @NotNull InkyMessageResolver standardInkyResolver() {
        return STANDARD_RESOLVER;
    }

    /**
     * Creates a new resolver builder
     * @return a builder
     */
    public static @NotNull InkyMessageResolver.Builder inkyResolver() {
        return new Builder();
    }

    private InkyMessageResolver(
            @NotNull Iterable<StyleTag> tags,
            @NotNull Collection<Replacer> replacers,
            @NotNull Iterable<SymbolicStyle> symbolics,
            @Nullable SymbolicStyle symbolicReset
    ) {
        this.tags = toMap(tags, StyleTag::namespace);
        this.replacers = replacers;
        this.symbolics = toMap(symbolics, SymbolicStyle::symbol);
        this.symbolicReset = symbolicReset;
    }

    private static <O, K> Map<K, O> toMap(Iterable<O> origin, Function<O, K> keyFunction) {
        Map<K, O> map = new HashMap<>();
        for (O obj : origin) {
            map.put(keyFunction.apply(obj), obj);
        }
        return map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    /**
     * Parses tags from a string using the color {@code (tag1)(tag2:value)(tag3:value parameters)(tagN...)}
     * @param tagsStr line of tags
     * @return list of parsed tags
     */
    public @NotNull List<StyleTag.Prepared> parseTags(@NotNull String tagsStr) {
        List<StyleTag.Prepared> preparedTags = new ArrayList<>();
        Matcher matcher = TAGS_PATTERN.matcher(tagsStr);
        while (matcher.find()) {
            StyleTag tag = tags.get(matcher.group(1));
            if (tag == null) continue;
            preparedTags.add(new StyleTag.Prepared(
                    tag,
                    matcher.group(2) == null ? "" : matcher.group(2),
                    matcher.group(3) == null ? "" : matcher.group(3)
            ));
        }
        return preparedTags;
    }

    /**
     * Applies symbolic style to the provided one
     * @param symbol style symbol
     * @param currentStyle style to be applied onto
     * @return provided style with applied symbolic style, or null if no styles were found with such symbol
     */
    public @Nullable Style applySymbolicStyle(char symbol, @NotNull Style currentStyle) {
        SymbolicStyle symbolic = symbolics.get(symbol);
        return symbolic == null ? null : symbolic.apply(currentStyle);
    }

    /**
     * Applies replacer onto a string and adds replacements into provided list of nodes
     * @param input string to replace in
     * @param nodes list of nodes to fill
     * @return string with replacers applied
     */
    @SuppressWarnings("UnstableApiUsage")
    @Contract(mutates = "param2")
    public @NotNull String applyReplacers(@NotNull String input, @NotNull List<RichNode> nodes) {
        String result = input;
        for (var replacer : replacers) {
            result = replacer.replace(result, nodes);
        }
        return result;
    }

    public @NotNull TreeSet<SymbolicStyle> readSymbolics(@NotNull Component text) {
        TreeSet<SymbolicStyle> symbolics = new TreeSet<>();
        Style style = text.style();
        boolean hasColor = false;
        for (var symb : this.symbolics.values()) {
            if (symb.isApplied(style)) {
                if (symb.hasColor()) hasColor = true;
                symbolics.add(symb);
            }
        }
        if (!hasColor && text.color() != null) {
            symbolics.add(new VirtualHexSymbolicStyle(text.color()));
        }
        return symbolics;
    }

    public @NotNull List<StyleTag.Prepared> readStyleTags(@NotNull Component text) {
        List<StyleTag.Prepared> tags = new ArrayList<>();
        for (var tag : this.tags.values()) {
            tags.addAll(tag.read(this, text));
        }
        return tags;
    }

    public @NotNull SymbolicStyle symbolicReset() {
        return symbolicReset;
    }

    public @NotNull InkyMessageResolver.Builder toBuilder() {
        return new Builder()
                .addTags(tags.values())
                .symbolicReset(symbolicReset)
                .addSymbolics(symbolics.values());
    }

    public static class Builder implements AbstractBuilder<InkyMessageResolver> {
        private Set<StyleTag> tags;
        private Set<Replacer> replacers;
        private Set<SymbolicStyle> symbolics;
        private SymbolicStyle symbolicReset;

        private Builder() {
            this.tags = new HashSet<>();
            this.replacers = new HashSet<>();
            this.symbolics = new HashSet<>();
        }

        @Contract("_ -> this")
        public @NotNull Builder replacers(@NotNull Replacer @NotNull ... replacers) {
            return replacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull Builder replacers(@NotNull Collection<@NotNull Replacer> replacers) {
            this.replacers = new HashSet<>(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addReplacer(@NotNull Replacer replacer) {
            this.replacers.add(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addReplacers(@NotNull Replacer @NotNull ... replacers) {
            return addReplacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull Builder addReplacers(@NotNull Iterable<@NotNull Replacer> replacers) {
            for (var replacer : replacers) addReplacer(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder tags(@NotNull StyleTag @NotNull ... styleTags) {
            return tags(Arrays.asList(styleTags));
        }

        @Contract("_ -> this")
        public @NotNull Builder tags(@NotNull Collection<@NotNull StyleTag> styleTags) {
            this.tags = new HashSet<>(styleTags);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addTag(@NotNull StyleTag tag) {
            this.tags.add(tag);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addTags(@NotNull StyleTag @NotNull ... tags) {
            return addTags(Arrays.asList(tags));
        }

        @Contract("_ -> this")
        public @NotNull Builder addTags(@NotNull Iterable<@NotNull StyleTag> tags) {
            for (var tag : tags) addTag(tag);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder symbolicReset(@NotNull SymbolicStyle symbolicReset) {
            symbolics.remove(this.symbolicReset);
            symbolics.add(symbolicReset);
            this.symbolicReset = symbolicReset;
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder symbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return symbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull Builder symbolics(@NotNull Collection<@NotNull SymbolicStyle> symbolics) {
            this.symbolics = new HashSet<>(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addSymbolic(@NotNull SymbolicStyle symbolics) {
            this.symbolics.add(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addSymbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return addSymbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull Builder addSymbolics(@NotNull Iterable<@NotNull SymbolicStyle> symbolics) {
            for (var symbolic : symbolics) addSymbolic(symbolic);
            return this;
        }

        @Override
        @Contract("-> new")
        public @NotNull InkyMessageResolver build() {
            Objects.requireNonNull(symbolicReset, "InkyMessageResolver requires symbolic reset to be provided");
            return new InkyMessageResolver(tags, replacers, symbolics, symbolicReset);
        }
    }
}
