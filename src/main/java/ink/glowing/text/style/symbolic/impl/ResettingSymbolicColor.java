package ink.glowing.text.style.symbolic.impl;

import ink.glowing.text.style.symbolic.SymbolicStyle;
import ink.glowing.text.utils.GeneralUtils;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextColor.color;

public final class ResettingSymbolicColor implements SymbolicStyle {
    private static final List<SymbolicStyle> NOTCHIAN_COLORS = List.of(
            breakingColor('0', BLACK),
            breakingColor('1', DARK_BLUE),
            breakingColor('2', DARK_GREEN),
            breakingColor('3', DARK_AQUA),
            breakingColor('4', DARK_RED),
            breakingColor('5', DARK_PURPLE),
            breakingColor('6', GOLD),
            breakingColor('7', GRAY),
            breakingColor('8', DARK_GRAY),
            breakingColor('9', BLUE),
            breakingColor('a', GREEN),
            breakingColor('b', AQUA),
            breakingColor('c', RED),
            breakingColor('d', LIGHT_PURPLE),
            breakingColor('e', YELLOW),
            breakingColor('f', WHITE)
    );

    private static final List<SymbolicStyle> BEDROCK_COLORS = GeneralUtils.buildList(
            NOTCHIAN_COLORS,
            List.of(
                    breakingColor('g', color(0xDDD605)),
                    breakingColor('h', color(0xE3D4D1)),
                    breakingColor('i', color(0xCECACA)),
                    breakingColor('j', color(0x443A3B)),
                    breakingColor('m', color(0x971607)),
                    breakingColor('n', color(0xB4684D)),
                    breakingColor('p', color(0xDEB12D)),
                    breakingColor('q', color(0x47A036)),
                    breakingColor('s', color(0x2CBAA8)),
                    breakingColor('t', color(0x21497B)),
                    breakingColor('u', color(0x9A5CC6))
            )
    );

    private final char symbol;
    private final TextColor color;
    private final Style cleanStyle;

    private ResettingSymbolicColor(char symbol, @NotNull TextColor color) {
        this.symbol = symbol;
        this.color = color;
        this.cleanStyle = Style.style(color);
    }

    public static @NotNull ResettingSymbolicColor breakingColor(char symbol, @NotNull TextColor color) {
        return new ResettingSymbolicColor(symbol, color);
    }

    public static @NotNull @Unmodifiable List<SymbolicStyle> notchianColors() {
        return NOTCHIAN_COLORS;
    }

    public static @NotNull @Unmodifiable List<SymbolicStyle> bedrockColors() {
        return BEDROCK_COLORS;
    }

    @Override
    public char symbol() {
        return symbol;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean isApplied(@NotNull Style inputStyle) {
        return color.equals(inputStyle.color());
    }

    @Override
    public @NotNull Style apply(@NotNull Style inputStyle) {
        return cleanStyle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResettingSymbolicColor that)) return false;
        return symbol == that.symbol && color.equals(that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, color);
    }

    @Override
    public String toString() {
        return "StandardSymbolicColor{" +
                "symbol=" + symbol +
                ", color=" + color +
                '}';
    }
}
