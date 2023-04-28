package ink.glowing.text;

import ink.glowing.text.replace.Replacer;
import ink.glowing.text.style.symbolic.SymbolicStyle;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.style.tag.standard.ClickTag;
import ink.glowing.text.style.tag.standard.ColorTag;
import ink.glowing.text.style.tag.standard.DecorTag;
import ink.glowing.text.style.tag.standard.FontTag;
import ink.glowing.text.style.tag.standard.GradientTag;
import ink.glowing.text.style.tag.standard.HoverTag;
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
import static ink.glowing.text.style.symbolic.SymbolicStyle.*;

final class IMResolverImpl implements InkyMessage.Resolver {
    static final InkyMessage.Resolver STANDARD_RESOLVER = InkyMessage.Resolver.resolver()
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

    private final Map<String, StyleTag<?>> tags;
    private final Collection<Replacer> replacers;
    private final Map<Character, SymbolicStyle> symbolics;
    private final SymbolicStyle symbolicReset;

    IMResolverImpl(
            @NotNull Iterable<StyleTag<?>> tags,
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

    @Override
    public @Nullable StyleTag<?> getTag(@NotNull String namespace) {
        return tags.get(namespace);
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
        return symbolic == null ? null : symbolic.apply(currentStyle);
    }

    /**
     * Find replaceable spots in a string
     * @param input string to replace in
     * @return found spots
     */
    @Override
    public @NotNull TreeSet<Replacer.FoundSpot> findReplacements(@NotNull String input) {
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
                if (symb.hasColor()) hasColor = true;
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
        public boolean hasColor() {
            return true;
        }

        @Override
        public boolean isApplied(@NotNull Style inputStyle) {
            return color.equals(inputStyle.color());
        }

        @Override
        public @NotNull Style apply(@NotNull Style inputStyle) {
            return inputStyle.color(color);
        }

        @Override
        public @NotNull String asFormatted() {
            return "&" + color.asHexString();
        }
    }
}
