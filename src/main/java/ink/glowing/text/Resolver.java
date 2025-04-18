package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.placeholder.PlaceholderGetter;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.symbolic.SymbolicStyle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import static ink.glowing.text.placeholder.PlaceholderGetter.composePlaceholderGetters;
import static ink.glowing.text.placeholder.PlaceholderGetter.placeholderGetter;
import static ink.glowing.text.placeholder.StandardPlaceholders.*;
import static net.kyori.adventure.text.format.Style.style;

@ApiStatus.Internal
final class Resolver implements InkyMessage.Resolver {
    private static final PlaceholderGetter REQUIRED_PLACEHOLDERS = placeholderGetter(
            langPlaceholder(),
            keybindPlaceholder(),
            scorePlaceholder(),
            selectorPlaceholder()
    );

    private final Map<String, Modifier<?>> modifiers;
    private final Map<String, Placeholder> placeholders;
    private final Collection<Replacer> replacers;
    private final Map<Character, SymbolicStyle> symbolics;
    private final SymbolicStyle symbolicReset;

    private final PlaceholderGetter phGetter;

    Resolver(
            @NotNull Set<Modifier<?>> modifiers,
            @NotNull Set<Placeholder> placeholders,
            @NotNull Set<Replacer> replacers,
            @NotNull Set<SymbolicStyle> symbolics,
            @NotNull SymbolicStyle symbolicReset
    ) {
        this.modifiers = toMap(modifiers, Modifier::name);
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
    public @Nullable Modifier<?> findModifier(@NotNull String name) {
        return modifiers.get(name);
    }

    @Override
    public @Nullable Placeholder findPlaceholder(@NotNull String name) {
        return phGetter.findPlaceholder(name);
    }

    @Override
    public @Nullable Style applySymbolicStyle(char symbol, @NotNull Style currentStyle) {
        SymbolicStyle symbolic = symbolics.get(symbol);
        if (symbolic == null) return null;
        return symbolic.resets()
                ? symbolic.base()
                : currentStyle.merge(symbolic.base());
    }

    @Override
    public @NotNull TreeSet<Replacer.FoundSpot> matchReplacements(@NotNull String input) {
        TreeSet<Replacer.FoundSpot> spots = new TreeSet<>();
        for (var replacer : replacers) {
            spots.addAll(replacer.findSpots(input));
        }
        return spots;
    }

    @Override
    public @NotNull TreeSet<SymbolicStyle> readSymbolics(@NotNull Style style) {
        // TODO StyleBuilder?
        TreeSet<SymbolicStyle> found = new TreeSet<>();
        for (var symb : this.symbolics.values()) {
            if (symb.isApplied(style)) {
                style = style.unmerge(symb.base());
                found.add(symb);
                if (style.isEmpty()) return found;
            }
        }
        if (style.color() != null) { // If color is still merged
            found.add(new HexSymbolicStyle(style.color()));
        }
        return found;
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

    private static final class HexSymbolicStyle implements SymbolicStyle {
        private final @NotNull TextColor color;
        private final @NotNull Style cleanStyle;

        private HexSymbolicStyle(@NotNull TextColor color) {
            this.color = color;
            this.cleanStyle = style(color);
        }

        @Override
        public char symbol() {
            return '#';
        }

        @Override
        public boolean resets() {
            return true;
        }

        @Override
        public boolean isApplied(@NotNull Style at) {
            return color.equals(at.color());
        }

        @Override
        public @NotNull Style base() {
            return cleanStyle;
        }

        @Override
        public @NotNull String asFormatted() {
            return "&" + color.asHexString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (HexSymbolicStyle) obj;
            return Objects.equals(this.color, that.color) &&
                    Objects.equals(this.cleanStyle, that.cleanStyle);
        }

        @Override
        public int hashCode() {
            return Objects.hash(color, cleanStyle);
        }

        @Override
        public String toString() {
            return "HexSymbolicStyle[" +
                    "color=" + color + ", " +
                    "cleanStyle=" + cleanStyle +
                    ']';
        }
    }
}
