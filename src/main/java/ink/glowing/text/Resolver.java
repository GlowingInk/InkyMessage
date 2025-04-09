package ink.glowing.text;

import ink.glowing.text.placeholders.Placeholder;
import ink.glowing.text.placeholders.PlaceholderGetter;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.style.modifier.StyleModifier;
import ink.glowing.text.style.symbolic.SymbolicStyle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import static ink.glowing.text.placeholders.PlaceholderGetter.composePlaceholderGetters;
import static ink.glowing.text.placeholders.PlaceholderGetter.placeholderGetter;
import static ink.glowing.text.placeholders.StandardPlaceholders.keybindPlaceholder;
import static ink.glowing.text.placeholders.StandardPlaceholders.langPlaceholder;
import static net.kyori.adventure.text.format.Style.style;

@ApiStatus.Internal
final class Resolver implements InkyMessage.Resolver {
    private static final PlaceholderGetter REQUIRED_PLACEHOLDERS = placeholderGetter(
            langPlaceholder(),
            keybindPlaceholder()
    );

    private final Map<String, StyleModifier<?>> modifiers;
    private final Map<String, Placeholder> placeholders;
    private final Collection<Replacer> replacers;
    private final Map<Character, SymbolicStyle> symbolics;
    private final SymbolicStyle symbolicReset;

    private final PlaceholderGetter phGetter;

    Resolver(
            @NotNull Set<StyleModifier<?>> modifiers,
            @NotNull Set<Placeholder> placeholders,
            @NotNull Set<Replacer> replacers,
            @NotNull Set<SymbolicStyle> symbolics,
            @NotNull SymbolicStyle symbolicReset
    ) {
        this.modifiers = toMap(modifiers, StyleModifier::name);
        this.placeholders = toMap(placeholders, Placeholder::name);
        this.replacers = replacers;
        this.symbolics = toMap(symbolics, SymbolicStyle::symbol);
        this.symbolicReset = symbolicReset;
        this.symbolics.put(symbolicReset.symbol(), symbolicReset);

        if (placeholders.isEmpty()) {
            phGetter = REQUIRED_PLACEHOLDERS;
        } else {
            phGetter = composePlaceholderGetters(
                    placeholderGetter(placeholders),
                    REQUIRED_PLACEHOLDERS
            );
        }
    }

    private static <O, K> Map<K, O> toMap(Collection<O> origin, Function<O, K> keyFunction) {
        Map<K, O> map = new HashMap<>(origin.size());
        for (O obj : origin) {
            map.put(keyFunction.apply(obj), obj);
        }
        return map;
    }

    @Override
    public @Nullable StyleModifier<?> findModifier(@NotNull String name) {
        return modifiers.get(name);
    }

    @Override
    public @Nullable Placeholder findPlaceholder(@NotNull String name) {
        return phGetter.findPlaceholder(name);
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
    public @NotNull List<String> readStyleModifiers(@NotNull Component text) {
        List<String> modifiers = new ArrayList<>();
        for (var modifier : this.modifiers.values()) {
            modifiers.addAll(modifier.read(this, text));
        }
        return modifiers;
    }

    @Override
    public @NotNull SymbolicStyle symbolicReset() {
        return symbolicReset;
    }

    @Override
    public @NotNull InkyMessage.ResolverBuilder toBuilder() {
        return new InkyMessage.ResolverBuilder()
                .symbolicReset(symbolicReset.symbol())
                .addSymbolics(symbolics.values())
                .addPlaceholders(placeholders.values())
                .addModifiers(modifiers.values());
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
