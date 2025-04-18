package ink.glowing.text.symbolic;

import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@ApiStatus.Internal
record SimpleSymbolicReset(char symbol) implements SymbolicStyle {
    @Override
    public boolean resets() {
        return true;
    }

    @Override
    public boolean isApplied(@NotNull Style at) {
        return false;
    }

    @Override
    public @NotNull Style base() {
        return Style.empty();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimpleSymbolicReset(char otherSymbol) && otherSymbol == symbol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    @Override
    public String toString() {
        return "SimpleSymbolicReset[" +
                "symbol=" + symbol + ", " +
                "decoration=reset" +
                ']';
    }
}
