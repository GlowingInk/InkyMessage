package ink.glowing.text.symbolic;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.format.Style.style;

@ApiStatus.Internal
final class ChainedSymbolicDecoration implements SymbolicStyle {
    private final char symbol;
    private final TextDecoration decoration;
    private final Style cleanStyle;

    ChainedSymbolicDecoration(char symbol, @NotNull TextDecoration decoration) {
        this.symbol = symbol;
        this.decoration = decoration;
        this.cleanStyle = style(decoration);
    }

    @Override
    public char symbol() {
        return symbol;
    }

    @Override
    public boolean resets() {
        return false;
    }

    @Override
    public boolean isApplied(@NotNull Style at) {
        return at.hasDecoration(decoration);
    }

    @Override
    public @NotNull Style base() {
        return cleanStyle;
    }

    @Override
    public @NotNull Style merge(@NotNull Style other) {
        return other.decoration(decoration, TextDecoration.State.TRUE);
    }

    @Override
    public @NotNull Style unmerge(@NotNull Style other) {
        return other.decoration(decoration, TextDecoration.State.NOT_SET);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof ChainedSymbolicDecoration other && other.decoration == decoration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, decoration);
    }
}
