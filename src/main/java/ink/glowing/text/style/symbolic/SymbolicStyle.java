package ink.glowing.text.style.symbolic;

import ink.glowing.text.style.symbolic.impl.BreakingSymbolicColor;
import ink.glowing.text.style.symbolic.impl.ChainedSymbolicDecoration;
import ink.glowing.text.style.symbolic.impl.SimpleSymbolicReset;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface SymbolicStyle extends Comparable<SymbolicStyle> {
    char symbol();

    boolean resets();

    boolean hasColor();

    boolean isApplied(@NotNull Style inputStyle);

    @NotNull Style apply(@NotNull Style inputStyle);

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

    static @NotNull SymbolicStyle notchianReset() {
        return SimpleSymbolicReset.NOTCHIAN_RESET;
    }

    static @NotNull @Unmodifiable List<SymbolicStyle> notchianDecorations() {
        return ChainedSymbolicDecoration.NOTCHIAN_DECORATIONS;
    }

    static @NotNull @Unmodifiable List<SymbolicStyle> notchianColors() {
        return BreakingSymbolicColor.NOTCHIAN_COLORS;
    }

    static @NotNull @Unmodifiable List<SymbolicStyle> bedrockColors() {
        return BreakingSymbolicColor.BEDROCK_COLORS;
    }
}
