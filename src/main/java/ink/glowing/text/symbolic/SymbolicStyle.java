package ink.glowing.text.symbolic;

import ink.glowing.text.Ink;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface SymbolicStyle extends Ink, Comparable<SymbolicStyle> {
    char symbol();

    boolean resets();

    boolean isApplied(@NotNull Style at);

    @NotNull Style base();

    @ApiStatus.NonExtendable
    default @NotNull String asFormatted() {
        return "&" + symbol();
    }

    @Override
    default int compareTo(@NotNull SymbolicStyle other) {
        if (symbol() == other.symbol()) return 0;
        if (resets()) {
            return other.resets() ? 0 : 1;
        }
        return Integer.compare(symbol(), other.symbol());
    }
}
