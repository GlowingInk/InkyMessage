package ink.glowing.text.style.symbolic.impl;

import ink.glowing.text.style.symbolic.SymbolicStyle;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static net.kyori.adventure.text.format.TextDecoration.*;

public final class ChainedSymbolicDecoration implements SymbolicStyle {
    public static final List<SymbolicStyle> NOTCHIAN_DECORATIONS = List.of(
            chainedDecoration('k', OBFUSCATED),
            chainedDecoration('l', BOLD),
            chainedDecoration('m', STRIKETHROUGH),
            chainedDecoration('n', UNDERLINED),
            chainedDecoration('o', ITALIC)
    );

    private final char symbol;
    private final TextDecoration decoration;

    private ChainedSymbolicDecoration(char symbol, @NotNull TextDecoration decoration) {
        this.symbol = symbol;
        this.decoration = decoration;
    }

    public static @NotNull ChainedSymbolicDecoration chainedDecoration(char symbol, @NotNull TextDecoration decoration) {
        return new ChainedSymbolicDecoration(symbol, decoration);
    }

    @Override
    public char symbol() {
        return symbol;
    }

    @Override
    public boolean resets() {
        return false;
    }

    @Override
    public boolean isApplied(@NotNull Style inputStyle) {
        return inputStyle.hasDecoration(decoration);
    }

    @Override
    public @NotNull Style apply(@NotNull Style inputStyle) {
        return inputStyle.decorate(decoration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChainedSymbolicDecoration that)) return false;
        return symbol == that.symbol && decoration == that.decoration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, decoration);
    }

    @Override
    public String toString() {
        return "StandardSymbolicDecoration{" +
                "symbol=" + symbol +
                ", decoration=" + decoration +
                '}';
    }
}
