package ink.glowing.text.style;

import ink.glowing.text.rich.RichNode;
import ink.glowing.text.style.tag.StyleTag;
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

import static ink.glowing.text.style.StyleModifier.symbolic;

public class StyleResolver {
    private static final Pattern TAGS_PATTERN = Pattern.compile("\\(([^:\\s]+)(?::(\\S+))?(?: ([^)]*))?\\)");

    private final Map<String, StyleTag> tags;
    private final Map<Character, UnaryOperator<Style>> symbolics;

    public static @NotNull StyleResolver styleResolver(@NotNull Iterable<StyleTag> tags, @NotNull Iterable<StyleModifier.Symbolic> symbolics) {
        return new StyleResolver(tags, symbolics);
    }

    public static @NotNull StyleResolver.Builder styleResolver() {
        return new Builder();
    }

    private StyleResolver(@NotNull Iterable<StyleTag> tags, @NotNull Iterable<StyleModifier.Symbolic> symbolics) {
        this.tags = toMap(tags, StyleTag::prefix, UnaryOperator.identity());
        this.symbolics = toMap(symbolics, StyleModifier.Symbolic::symbol, StyleModifier.Symbolic::mergerFunction);
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
                    matcher.group(3) == null ? RichNode.empty() : RichNode.richText(matcher.group(3), List.of())
            ));
        }
        return preparedTags;
    }

    public @Nullable Style mergeSymbolicStyle(char ch, @NotNull Style currentStyle) {
        UnaryOperator<Style> symbolic = symbolics.get(ch);
        if (symbolic == null) return null;
        return symbolic.apply(currentStyle);
    }

    public @NotNull StyleResolver.Builder toBuilder() {
        return new Builder()
                .tags(tags.values())
                .symbolics(symbolics.entrySet().stream().map((entry) -> symbolic(entry.getKey(), entry.getValue())).toList());
    }

    public static class Builder implements AbstractBuilder<StyleResolver> {
        private List<StyleTag> tags;
        private List<StyleModifier.Symbolic> symbolics;

        private Builder() {
            this.tags = new ArrayList<>();
            this.symbolics = new ArrayList<>();
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
        public @NotNull Builder addTag(StyleTag... tags) {
            return addTag(Arrays.asList(tags));
        }

        @Contract("_ -> this")
        public @NotNull Builder addTag(Iterable<StyleTag> tags) {
            for (var tag : tags) this.tags.add(tag);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder symbolics(@NotNull StyleModifier.Symbolic... symbolics) {
            return symbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull Builder symbolics(@NotNull Collection<StyleModifier.Symbolic> symbolics) {
            this.symbolics = new ArrayList<>(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addSymbolic(StyleModifier.Symbolic symbolics) {
            this.symbolics.add(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addSymbolic(StyleModifier.Symbolic... symbolics) {
            return addSymbolic(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull Builder addSymbolic(Iterable<StyleModifier.Symbolic> symbolics) {
            for (var symbolic : symbolics) this.symbolics.add(symbolic);
            return this;
        }

        @Override
        public @NotNull StyleResolver build() {
            return new StyleResolver(tags, symbolics);
        }
    }
}
