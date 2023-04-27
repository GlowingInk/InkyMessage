package ink.glowing.text.style.symbolic.impl;

import ink.glowing.text.style.symbolic.SymbolicStyle;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record SimpleSymbolicReset(char symbol) implements SymbolicStyle {
    public static final SimpleSymbolicReset NOTCHIAN_RESET = new SimpleSymbolicReset('r');

    @Override
    public boolean resets() {
        return true;
    }

    @Override
    public boolean hasColor() {
        return false;
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
        return obj instanceof SimpleSymbolicReset ssr && ssr.symbol == symbol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return "SymbolicReset{" +
                "symbol=" + symbol +
                ", decoration=reset" +
                '}';
    }
}
