package ink.glowing.text;

import ink.glowing.text.placeholders.Placeholder;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.style.symbolic.SymbolicStyle;
import ink.glowing.text.style.tag.StyleTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;

import static ink.glowing.text.replace.StandardReplacers.urlReplacer;
import static ink.glowing.text.style.symbolic.StandardSymbolicStyles.*;
import static ink.glowing.text.style.tag.standard.ClickTag.clickTag;
import static ink.glowing.text.style.tag.standard.ColorTag.colorTag;
import static ink.glowing.text.style.tag.standard.DecorTag.decorTag;
import static ink.glowing.text.style.tag.standard.FontTag.fontTag;
import static ink.glowing.text.style.tag.standard.HoverTag.hoverTag;
import static net.kyori.adventure.text.format.Style.style;

final class InkyResolverImpl implements InkyMessage.Resolver {
    static final InkyMessage.Resolver STANDARD_RESOLVER = InkyMessage.resolver()
            .addTags(colorTag(),
                    hoverTag(),
                    clickTag(),
                    fontTag(),
                    decorTag())
            .addSymbolics(notchianColors())
            .addSymbolics(notchianDecorations())
            .symbolicReset(notchianReset())
            .addReplacer(urlReplacer())
            .build();

    private final Map<String, StyleTag<?>> tags;
    private final Map<String, Placeholder> placeholders;
    private final Collection<Replacer> replacers;
    private final Map<Character, SymbolicStyle> symbolics;
    private final SymbolicStyle symbolicReset;

    InkyResolverImpl(
            @NotNull Iterable<StyleTag<?>> tags,
            @NotNull Collection<Placeholder> placeholders,
            @NotNull Collection<Replacer> replacers,
            @NotNull Iterable<SymbolicStyle> symbolics,
            @NotNull SymbolicStyle symbolicReset
    ) {
        this.tags = toMap(tags, StyleTag::name);
        this.placeholders = toMap(placeholders, Placeholder::name);
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

    @Override
    public @Nullable StyleTag<?> findTag(@NotNull String name) {
        return tags.get(name);
    }

    @Override
    public @Nullable Placeholder findPlaceholder(@NotNull String name) {
        return placeholders.get(name);
    }

    /**
     * Applies symbolic style to the provided one
     * @param symbol style symbol
     * @param currentStyle style to be applied onto
     * @return provided style with applied symbolic style, or null if no styles were found with such symbol
     */
    @Override
    public @Nullable Style applySymbolicStyle(char symbol, @NotNull Style currentStyle) {
        SymbolicStyle symbolic = symbolics.get(symbol);
        return symbolic == null ? null : symbolic.merge(currentStyle);
    }

    /**
     * Find replaceable spots in a string
     * @param input string to replace in
     * @return found spots
     */
    @Override
    public @NotNull TreeSet<Replacer.FoundSpot> matchReplacements(@NotNull String input) {
        TreeSet<Replacer.FoundSpot> spots = new TreeSet<>();
        for (var replacer : replacers) {
            spots.addAll(replacer.findSpots(input));
        }
        return spots;
    }

    @Override
    public @NotNull TreeSet<SymbolicStyle> readSymbolics(@NotNull Component text) {
        TreeSet<SymbolicStyle> symbolics = new TreeSet<>();
        Style style = text.style();
        boolean hasColor = false;
        for (var symb : this.symbolics.values()) {
            if (symb.isApplied(style)) {
                if (symb.base().color() != null) hasColor = true;
                symbolics.add(symb);
            }
        }
        if (!hasColor && text.color() != null) {
            symbolics.add(new HexSymbolicStyle(text.color()));
        }
        return symbolics;
    }

    @Override
    public @NotNull List<String> readStyleTags(@NotNull Component text) {
        List<String> tags = new ArrayList<>();
        for (var tag : this.tags.values()) {
            tags.addAll(tag.read(this, text));
        }
        return tags;
    }

    @Override
    public @NotNull SymbolicStyle symbolicReset() {
        return symbolicReset;
    }

    @Override
    public @NotNull InkyMessage.ResolverBuilder toBuilder() {
        return new InkyMessage.ResolverBuilder()
                .addTags(tags.values())
                .symbolicReset(symbolicReset)
                .addSymbolics(symbolics.values());
    }

    private record HexSymbolicStyle(@NotNull TextColor color) implements SymbolicStyle {
        @Override
        public char symbol() {
            return '#';
        }

        @Override
        public boolean resets() {
            return true;
        }

        @Override
        public boolean isApplied(@NotNull Style inputStyle) {
            return color.equals(inputStyle.color());
        }

        @Override
        public @NotNull Style base() {
            return style(color);
        }

        @Override
        public @NotNull Style merge(@NotNull Style inputStyle) {
            return inputStyle.color(color);
        }

        @Override
        public @NotNull String asFormatted() {
            return "&" + color.asHexString();
        }
    }
}
