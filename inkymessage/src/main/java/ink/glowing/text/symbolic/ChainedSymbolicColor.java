package ink.glowing.text.symbolic;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@ApiStatus.Internal
final class ChainedSymbolicColor implements SymbolicStyle {
    private final char symbol;
    private final TextColor color;
    private final Style cleanStyle;

    ChainedSymbolicColor(char symbol, @NotNull TextColor color) {
        this.symbol = symbol;
        this.color = color;
        this.cleanStyle = Style.style(color);
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
    public boolean isApplied(@NotNull Style at) {
        return color.equals(at.color());
    }

    @Override
    public @NotNull Style base() {
        return cleanStyle;
    }

    @Override
    public @NotNull Style merge(@NotNull Style other) {
        return other.color(color);
    }

    @Override
    public @NotNull Style unmerge(@NotNull Style other) {
        return other.color(null);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof ChainedSymbolicColor other && other.color.equals(color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, color);
    }
}
