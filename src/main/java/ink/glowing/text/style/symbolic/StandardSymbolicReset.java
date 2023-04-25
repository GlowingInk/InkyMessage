package ink.glowing.text.style.symbolic;

import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record StandardSymbolicReset(char symbol) implements SymbolicStyle {
    private static final StandardSymbolicReset NOTCHIAN_RESET = new StandardSymbolicReset('r');

    public static @NotNull SymbolicStyle notchianReset() {
        return NOTCHIAN_RESET;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean isApplied(@NotNull Style inputStyle) {
        return false;
    }

    @Override
    public @NotNull Style apply(@NotNull Style inputStyle) {
        return Style.empty();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StandardSymbolicReset ssr && ssr.symbol == symbol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return "StandardSymbolicDecoration{" +
                "symbol=" + symbol +
                ", decoration=reset" +
                '}';
    }
}
