package ink.glowing.text.style.symbolic.impl;

import ink.glowing.text.style.symbolic.SymbolicStyle;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record VirtualHexSymbolicStyle(@NotNull TextColor color) implements SymbolicStyle {
    @Override
    public char symbol() {
        return '#';
    }

    @Override
    public boolean resets() {
        return true;
    }

    @Override
    public boolean hasColor() {
        return true;
    }

    @Override
    public boolean isApplied(@NotNull Style inputStyle) {
        return color.equals(inputStyle.color());
    }

    @Override
    public @NotNull Style apply(@NotNull Style inputStyle) {
        return inputStyle.color(color);
    }

    @Override
    public @NotNull String asFormatted() {
        return "&" + color.asHexString();
    }
}
