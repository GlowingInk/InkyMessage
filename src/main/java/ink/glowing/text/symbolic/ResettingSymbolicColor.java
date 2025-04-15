package ink.glowing.text.symbolic;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@ApiStatus.Internal
final class ResettingSymbolicColor implements SymbolicStyle {
    private final char symbol;
    private final TextColor color;
    private final Style cleanStyle;

    ResettingSymbolicColor(char symbol, @NotNull TextColor color) {
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
        return true;
    }

    @Override
    public boolean isApplied(@NotNull Style inputStyle) {
        return color.equals(inputStyle.color());
    }

    @Override
    public @NotNull Style base() {
        return cleanStyle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResettingSymbolicColor that)) return false;
        return symbol == that.symbol && color.equals(that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, color);
    }

    @Override
    public String toString() {
        return "ResettingSymbolicColor[" +
                "symbol=" + symbol + ", " +
                "color=" + color +
                ']';
    }
}
