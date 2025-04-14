package ink.glowing.text.symbolic;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.format.Style.style;

@ApiStatus.Internal
final class ChainedSymbolicDecoration implements SymbolicStyle {
    private final char symbol;
    private final TextDecoration decoration;
    private final Style cleanStyle;

    ChainedSymbolicDecoration(char symbol, @NotNull TextDecoration decoration) {
        this.symbol = symbol;
        this.decoration = decoration;
        this.cleanStyle = style(decoration);
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
    public @NotNull Style base() {
        return cleanStyle;
    }

    @Override
    public @NotNull Style merge(@NotNull Style inputStyle) {
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
        return "ChainedSymbolicDecoration{" +
                "symbol=" + symbol +
                ", decoration=" + decoration +
                '}';
    }
}
