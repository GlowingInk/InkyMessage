package ink.glowing.text.style.symbolic;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

import static net.kyori.adventure.text.format.TextDecoration.*;

public final class StandardSymbolicDecoration implements SymbolicStyle {
    private static final List<SymbolicStyle> NOTCHIAN_DECORATIONS = List.of(
            standardDecoration('k', OBFUSCATED),
            standardDecoration('l', BOLD),
            standardDecoration('m', STRIKETHROUGH),
            standardDecoration('n', UNDERLINED),
            standardDecoration('o', ITALIC)
    );

    private final char symbol;
    private final TextDecoration decoration;

    private StandardSymbolicDecoration(char symbol, @NotNull TextDecoration decoration) {
        this.symbol = symbol;
        this.decoration = decoration;
    }

    public static @NotNull StandardSymbolicDecoration standardDecoration(char symbol, @NotNull TextDecoration decoration) {
        return new StandardSymbolicDecoration(symbol, decoration);
    }

    public static @NotNull @Unmodifiable List<SymbolicStyle> notchianDecorations() {
        return NOTCHIAN_DECORATIONS;
    }

    @Override
    public char symbol() {
        return symbol;
    }

    @Override
    public boolean reset() {
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
        if (!(o instanceof StandardSymbolicDecoration that)) return false;
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
