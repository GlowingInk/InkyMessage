package ink.glowing.text.style.symbolic;

import ink.glowing.text.utils.GeneralUtils;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.*;

public final class StandardSymbolicStyles {
    private static final List<SymbolicStyle> NOTCHIAN_COLORS = List.of(
            resettingColor('0', BLACK),
            resettingColor('1', DARK_BLUE),
            resettingColor('2', DARK_GREEN),
            resettingColor('3', DARK_AQUA),
            resettingColor('4', DARK_RED),
            resettingColor('5', DARK_PURPLE),
            resettingColor('6', GOLD),
            resettingColor('7', GRAY),
            resettingColor('8', DARK_GRAY),
            resettingColor('9', BLUE),
            resettingColor('a', GREEN),
            resettingColor('b', AQUA),
            resettingColor('c', RED),
            resettingColor('d', LIGHT_PURPLE),
            resettingColor('e', YELLOW),
            resettingColor('f', WHITE)
    );

    private static final Collection<SymbolicStyle> BEDROCK_COLORS = GeneralUtils.concatImmutable(
            HashSet::new,
            NOTCHIAN_COLORS,
            List.of(
                    resettingColor('g', color(0xDDD605)),
                    resettingColor('h', color(0xE3D4D1)),
                    resettingColor('i', color(0xCECACA)),
                    resettingColor('j', color(0x443A3B)),
                    resettingColor('m', color(0x971607)),
                    resettingColor('n', color(0xB4684D)),
                    resettingColor('p', color(0xDEB12D)),
                    resettingColor('q', color(0x47A036)),
                    resettingColor('s', color(0x2CBAA8)),
                    resettingColor('t', color(0x21497B)),
                    resettingColor('u', color(0x9A5CC6))
            )
    );

    private static final Collection<SymbolicStyle> BEDROCK_DECORATIONS = List.of(
            chainedDecoration('k', OBFUSCATED),
            chainedDecoration('l', BOLD),
            chainedDecoration('o', ITALIC)
    );

    private static final Collection<SymbolicStyle> NOTCHIAN_DECORATIONS = GeneralUtils.concatImmutable(
            HashSet::new,
            BEDROCK_DECORATIONS,
            Arrays.asList(
                    chainedDecoration('m', STRIKETHROUGH),
                    chainedDecoration('n', UNDERLINED)
            )
    );

    private static final SymbolicStyle NOTCHIAN_RESET = simpleReset('r');

    private StandardSymbolicStyles() {}

    public static @NotNull SymbolicStyle notchianReset() {
        return NOTCHIAN_RESET;
    }

    public static @NotNull @Unmodifiable Collection<SymbolicStyle> notchianDecorations() {
        return NOTCHIAN_DECORATIONS;
    }

    public static @NotNull @Unmodifiable Collection<SymbolicStyle> notchianColors() {
        return NOTCHIAN_COLORS;
    }

    public static @NotNull @Unmodifiable Collection<SymbolicStyle> bedrockDecorations() {
        return BEDROCK_DECORATIONS;
    }

    public static @NotNull @Unmodifiable Collection<SymbolicStyle> bedrockColors() {
        return BEDROCK_COLORS;
    }

    public static @NotNull ChainedSymbolicDecoration chainedDecoration(char symbol, @NotNull TextDecoration decoration) {
        return new ChainedSymbolicDecoration(symbol, decoration);
    }

    public static @NotNull ResettingSymbolicColor resettingColor(char symbol, @NotNull TextColor color) {
        return new ResettingSymbolicColor(symbol, color);
    }

    public static @NotNull SymbolicStyle simpleReset(char symbol) {
        return new SimpleSymbolicReset(symbol);
    }
}
