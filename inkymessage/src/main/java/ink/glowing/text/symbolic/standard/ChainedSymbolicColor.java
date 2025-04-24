package ink.glowing.text.symbolic.standard;

import ink.glowing.text.symbolic.SymbolicStyle;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
record ChainedSymbolicColor(char symbol, @NotNull TextColor color) implements SymbolicStyle {
    @Override
    public boolean resets() {
        return false;
    }

    @Override
    public boolean isApplied(@NotNull Style at) {
        return color.equals(at.color());
    }

    @Override
    public @NotNull Style merge(@NotNull Style other) {
        return other.color(color);
    }

    @Override
    public @NotNull Style unmerge(@NotNull Style other) {
        return other.color(null);
    }
}
