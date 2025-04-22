package ink.glowing.text.symbolic;

import ink.glowing.text.Ink;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SymbolicStyle extends Ink, Comparable<SymbolicStyle>, SymbolicStyleFinder {
    char symbol();

    boolean resets();

    boolean isApplied(@NotNull Style at);

    @NotNull Style base();

    default @NotNull Style merge(@NotNull Style other) {
        return resets() ? base() : other.merge(base());
    }

    default @NotNull Style unmerge(@NotNull Style other) {
        return other.unmerge(base());
    }

    @Override
    default @Nullable SymbolicStyle findSymbolicStyle(char symbol) {
        return symbol() == symbol ? this : null;
    }

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
