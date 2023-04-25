package ink.glowing.text.style.symbolic;

import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public interface SymbolicStyle {
    char symbol();

    boolean reset();

    boolean isApplied(@NotNull Style inputStyle);

    @NotNull Style apply(@NotNull Style inputStyle);
}
