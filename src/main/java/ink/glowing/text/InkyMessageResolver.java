package ink.glowing.text;

import ink.glowing.text.replace.Replacer;
import ink.glowing.text.replace.UrlReplacer;
import ink.glowing.text.rich.RichNode;
import ink.glowing.text.style.SymbolicStyle;
import ink.glowing.text.style.tag.ClickTag;
import ink.glowing.text.style.tag.ColorTag;
import ink.glowing.text.style.tag.DecorTag;
import ink.glowing.text.style.tag.FontTag;
import ink.glowing.text.style.tag.HoverTag;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ink.glowing.text.style.SymbolicStyle.symbolicStyle;
import static ink.glowing.text.utils.Utils.nodeId;

public class InkyMessageResolver {
    private static final Pattern TAGS_PATTERN = Pattern.compile("\\(([^:\\s]+)(?::(\\S+))?(?: ([^)]*))?\\)");

    private static final InkyMessageResolver STANDARD_RESOLVER = inkyResolver()
            .tags(Arrays.asList(
                    ColorTag.colorTag(),
                    HoverTag.hoverTag(),
                    ClickTag.clickTag(),
                    FontTag.fontTag(),
                    DecorTag.decorTag()))
            .addSymbolics(SymbolicStyle.legacyColors())
            .addSymbolics(SymbolicStyle.legacyDecorations())
            .addReplacer(UrlReplacer.urlReplacer())
            .build();

    private final Map<String, StyleTag> tags;
    private final Map<Character, UnaryOperator<Style>> symbolics;
    private final List<Replacer.Literal> literalReplacers;
    private final List<Replacer.Regex> regexReplacers;

    public static @NotNull InkyMessageResolver standardInkyResolver() {
        return STANDARD_RESOLVER;
    }

    public static @NotNull InkyMessageResolver.Builder inkyResolver() {
        return new Builder();
    }

    private InkyMessageResolver(
            @NotNull Iterable<StyleTag> tags,
            @NotNull Iterable<SymbolicStyle> symbolics,
            @NotNull List<Replacer.Literal> literalReplacers,
            @NotNull List<Replacer.Regex> regexReplacers
    ) {
        this.tags = toMap(tags, StyleTag::prefix, UnaryOperator.identity());
        this.symbolics = toMap(symbolics, SymbolicStyle::symbol, SymbolicStyle::mergerFunction);
        this.literalReplacers = literalReplacers;
        this.regexReplacers = regexReplacers;
    }

    private static <O, K, V> Map<K, V> toMap(Iterable<O> origin, Function<O, K> keyFunction, Function<O, V> valueFunction) {
        Map<K, V> map = new HashMap<>();
        for (O obj : origin) {
            map.put(keyFunction.apply(obj), valueFunction.apply(obj));
        }
        return map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    public @Nullable StyleTag getTag(@NotNull String name) {
        return tags.get(name);
    }

    public @NotNull List<StyleTag.Prepared> parseTags(@NotNull String tagsStr) {
        List<StyleTag.Prepared> preparedTags = new ArrayList<>();
        Matcher matcher = TAGS_PATTERN.matcher(tagsStr);
        while (matcher.find()) {
            StyleTag tag = getTag(matcher.group(1));
            if (tag == null) continue;
            preparedTags.add(new StyleTag.Prepared(
                    tag,
                    matcher.group(2) == null ? "" : matcher.group(2),
                    matcher.group(3) == null ? "" : matcher.group(3)
            ));
        }
        return preparedTags;
    }

    public @Nullable Style mergeSymbolicStyle(char ch, @NotNull Style currentStyle) {
        UnaryOperator<Style> symbolic = symbolics.get(ch);
        if (symbolic == null) return null;
        return symbolic.apply(currentStyle);
    }

    public @NotNull String applyReplacers(@NotNull String input, @NotNull List<RichNode> nodes) {
        String result = input;
        for (var replacer : regexReplacers) {
            result = replacer.search().matcher(result).replaceAll(match -> {
                nodes.add(replacer.replace(match.group()));
                return nodeId(nodes.size() - 1);
            });
        }
        for (var replacer : literalReplacers) {
            result = Utils.replaceEach(result, replacer.search(), () -> {
                nodes.add(replacer.replace(replacer.search()));
                return nodeId(nodes.size() - 1);
            });
        }
        return result;
    }

    public @NotNull InkyMessageResolver.Builder toBuilder() {
        return new Builder()
                .tags(tags.values())
                .symbolics(symbolics.entrySet().stream().map((entry) -> symbolicStyle(entry.getKey(), entry.getValue())).toList());
    }

    public static class Builder implements AbstractBuilder<InkyMessageResolver> {
        private List<StyleTag> tags;
        private List<SymbolicStyle> symbolics;
        private List<Replacer.Literal> literalReplacers;
        private List<Replacer.Regex> regexReplacers;

        private Builder() {
            this.tags = new ArrayList<>();
            this.symbolics = new ArrayList<>();
            this.literalReplacers = new ArrayList<>();
            this.regexReplacers = new ArrayList<>();
        }

        @Contract("_ -> this")
        public @NotNull Builder literalReplacers(@NotNull Replacer.Literal... literalReplacers) {
            return literalReplacers(Arrays.asList(literalReplacers));
        }

        @Contract("_ -> this")
        public @NotNull Builder literalReplacers(@NotNull Collection<Replacer.Literal> literalReplacers) {
            this.literalReplacers = new ArrayList<>(literalReplacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder regexReplacers(@NotNull Replacer.Regex... regexReplacers) {
            return regexReplacers(Arrays.asList(regexReplacers));
        }

        @Contract("_ -> this")
        public @NotNull Builder regexReplacers(@NotNull Collection<Replacer.Regex> regexReplacers) {
            this.regexReplacers = new ArrayList<>(regexReplacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addReplacer(Replacer<?> replacer) {
            if (replacer instanceof Replacer.Literal literal) {
                literalReplacers.add(literal);
            } else if (replacer instanceof Replacer.Regex regex) {
                regexReplacers.add(regex);
            }
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addReplacers(Replacer<?>... replacers) {
            return addReplacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull Builder addReplacers(Iterable<Replacer<?>> replacers) {
            for (var replacer : replacers) addReplacer(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder tags(@NotNull StyleTag... styleTags) {
            return tags(Arrays.asList(styleTags));
        }

        @Contract("_ -> this")
        public @NotNull Builder tags(@NotNull Collection<StyleTag> styleTags) {
            this.tags = new ArrayList<>(styleTags);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addTag(StyleTag tag) {
            this.tags.add(tag);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addTags(StyleTag... tags) {
            return addTags(Arrays.asList(tags));
        }

        @Contract("_ -> this")
        public @NotNull Builder addTags(Iterable<StyleTag> tags) {
            for (var tag : tags) addTag(tag);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder symbolics(@NotNull SymbolicStyle... symbolics) {
            return symbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull Builder symbolics(@NotNull Collection<SymbolicStyle> symbolics) {
            this.symbolics = new ArrayList<>(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addSymbolic(SymbolicStyle symbolics) {
            this.symbolics.add(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addSymbolics(SymbolicStyle... symbolics) {
            return addSymbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull Builder addSymbolics(Iterable<SymbolicStyle> symbolics) {
            for (var symbolic : symbolics) addSymbolic(symbolic);
            return this;
        }

        @Override
        public @NotNull InkyMessageResolver build() {
            return new InkyMessageResolver(tags, symbolics, literalReplacers, regexReplacers);
        }
    }
}
