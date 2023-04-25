package ink.glowing.text.style.symbolic;

import ink.glowing.text.utils.GeneralUtils;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextColor.color;

public final class StandardSymbolicColor implements SymbolicStyle {
    private static final List<SymbolicStyle> NOTCHIAN_COLORS = List.of(
            standardColor('0', BLACK),
            standardColor('1', DARK_BLUE),
            standardColor('2', DARK_GREEN),
            standardColor('3', DARK_AQUA),
            standardColor('4', DARK_RED),
            standardColor('5', DARK_PURPLE),
            standardColor('6', GOLD),
            standardColor('7', GRAY),
            standardColor('8', DARK_GRAY),
            standardColor('9', BLUE),
            standardColor('a', GREEN),
            standardColor('b', AQUA),
            standardColor('c', RED),
            standardColor('d', LIGHT_PURPLE),
            standardColor('e', YELLOW),
            standardColor('f', WHITE)
    );

    private static final List<SymbolicStyle> BEDROCK_COLORS = GeneralUtils.buildList(
            NOTCHIAN_COLORS,
            List.of(
                    standardColor('g', color(0xDDD605)),
                    standardColor('h', color(0xE3D4D1)),
                    standardColor('i', color(0xCECACA)),
                    standardColor('j', color(0x443A3B)),
                    standardColor('m', color(0x971607)),
                    standardColor('n', color(0xB4684D)),
                    standardColor('p', color(0xDEB12D)),
                    standardColor('q', color(0x47A036)),
                    standardColor('s', color(0x2CBAA8)),
                    standardColor('t', color(0x21497B)),
                    standardColor('u', color(0x9A5CC6))
            )
    );

    private final char symbol;
    private final TextColor color;
    private final Style cleanStyle;

    private StandardSymbolicColor(char symbol, @NotNull TextColor color) {
        this.symbol = symbol;
        this.color = color;
        this.cleanStyle = Style.style(color);
    }

    public static @NotNull StandardSymbolicColor standardColor(char symbol, @NotNull TextColor color) {
        return new StandardSymbolicColor(symbol, color);
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
        if (!(o instanceof StandardSymbolicColor that)) return false;
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
