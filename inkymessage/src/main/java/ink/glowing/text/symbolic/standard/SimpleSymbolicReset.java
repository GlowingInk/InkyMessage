package ink.glowing.text.symbolic.standard;

import ink.glowing.text.symbolic.SymbolicStyle;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import static ink.glowing.text.symbolic.standard.StandardSymbolicStyles.resetDecorations;

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
    public @NotNull Style merge(@NotNull Style other) {
        return other.decorations(resetDecorations()).color(null);
    }

    @Override
    public @NotNull Style unmerge(@NotNull Style other) {
        return other;
    }
}
