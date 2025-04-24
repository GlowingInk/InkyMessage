package ink.glowing.text.symbolic.standard;

import ink.glowing.text.symbolic.SymbolicStyle;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
record ChainedSymbolicDecoration(char symbol, @NotNull TextDecoration decoration) implements SymbolicStyle {
    @Override
    public boolean resets() {
        return false;
    }

    @Override
    public boolean isApplied(@NotNull Style at) {
        return at.hasDecoration(decoration);
    }

    @Override
    public @NotNull Style merge(@NotNull Style other) {
        return other.decoration(decoration, TextDecoration.State.TRUE);
    }

    @Override
    public @NotNull Style unmerge(@NotNull Style other) {
        return other.decoration(decoration, TextDecoration.State.NOT_SET);
    }
}
