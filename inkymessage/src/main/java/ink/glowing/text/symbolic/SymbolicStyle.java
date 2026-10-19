package ink.glowing.text.symbolic;

import ink.glowing.text.Ink;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Style that is applied with a single symbol, like {@code &c} or {@code &l}.
 */
public non-sealed interface SymbolicStyle extends Ink, Comparable<SymbolicStyle>, SymbolicStyleFinder {
    /**
     * Symbol of this style.
     * @return the symbol
     */
    char symbol();

    /**
     * Checks if this style resets the previously applied styles.
     * @return does this style reset
     */
    boolean resets();

    /**
     * Checks if this style is applied to the provided style.
     * @param at style to check
     * @return is this style applied
     */
    boolean isApplied(@NotNull Style at);

    /**
     * Applies this style to the provided style.
     * @param other style to apply to
     * @return style with this style applied
     */
    @NotNull Style merge(@NotNull Style other);

    /**
     * Removes this style from the provided style.
     * @param other style to remove from
     * @return style without this style
     */
    @NotNull Style unmerge(@NotNull Style other);

    @Override
    default @Nullable SymbolicStyle findSymbolicStyle(char symbol) {
        return symbol() == symbol ? this : null;
    }

    /**
     * Formats this style as a string.
     * @return formatted style
     */
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
