package ink.glowing.text.style.symbolic;

import ink.glowing.text.style.symbolic.impl.BreakingSymbolicColor;
import ink.glowing.text.style.symbolic.impl.ChainedSymbolicDecoration;
import ink.glowing.text.style.symbolic.impl.SymbolicReset;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface SymbolicStyle {
    char symbol();

    boolean resets();

    boolean isApplied(@NotNull Style inputStyle);

    @NotNull Style apply(@NotNull Style inputStyle);

    static @NotNull SymbolicStyle notchianReset() {
        return SymbolicReset.NOTCHIAN_RESET;
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
