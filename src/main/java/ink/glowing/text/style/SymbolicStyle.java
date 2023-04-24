package ink.glowing.text.style;

import ink.glowing.text.utils.GeneralUtils;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.*;

public record SymbolicStyle(char symbol, @NotNull UnaryOperator<@NotNull Style> mergerFunction) {
    private static final List<SymbolicStyle> LEGACY_COLORS = List.of(
            symbolicStyle('0', (s) -> style(BLACK)),
            symbolicStyle('1', (s) -> style(DARK_BLUE)),
            symbolicStyle('2', (s) -> style(DARK_GREEN)),
            symbolicStyle('3', (s) -> style(DARK_AQUA)),
            symbolicStyle('4', (s) -> style(DARK_RED)),
            symbolicStyle('5', (s) -> style(DARK_PURPLE)),
            symbolicStyle('6', (s) -> style(GOLD)),
            symbolicStyle('7', (s) -> style(GRAY)),
            symbolicStyle('8', (s) -> style(DARK_GRAY)),
            symbolicStyle('9', (s) -> style(BLUE)),
            symbolicStyle('a', (s) -> style(GREEN)),
            symbolicStyle('b', (s) -> style(AQUA)),
            symbolicStyle('c', (s) -> style(RED)),
            symbolicStyle('d', (s) -> style(LIGHT_PURPLE)),
            symbolicStyle('e', (s) -> style(YELLOW)),
            symbolicStyle('f', (s) -> style(WHITE))
    );

    private static final List<SymbolicStyle> LEGACY_DECORATIONS = List.of(
            symbolicStyle('k', (s) -> s.decorate(OBFUSCATED)),
            symbolicStyle('l', (s) -> s.decorate(BOLD)),
            symbolicStyle('m', (s) -> s.decorate(STRIKETHROUGH)),
            symbolicStyle('n', (s) -> s.decorate(UNDERLINED)),
            symbolicStyle('o', (s) -> s.decorate(ITALIC)),
            symbolicStyle('r', (s) -> Style.empty())
    );

    private static final List<SymbolicStyle> BEDROCK_COLORS = GeneralUtils.buildList(
            LEGACY_COLORS,
            List.of(
                    symbolicStyle('g', (s) -> style(color(0xDDD605))),
                    symbolicStyle('h', (s) -> style(color(0xE3D4D1))),
                    symbolicStyle('i', (s) -> style(color(0xCECACA))),
                    symbolicStyle('j', (s) -> style(color(0x443A3B))),
                    symbolicStyle('m', (s) -> style(color(0x971607))),
                    symbolicStyle('n', (s) -> style(color(0xB4684D))),
                    symbolicStyle('p', (s) -> style(color(0xDEB12D))),
                    symbolicStyle('q', (s) -> style(color(0x47A036))),
                    symbolicStyle('s', (s) -> style(color(0x2CBAA8))),
                    symbolicStyle('t', (s) -> style(color(0x21497B))),
                    symbolicStyle('u', (s) -> style(color(0x9A5CC6)))
            )
    );

    public static @NotNull SymbolicStyle symbolicStyle(char symbol, @NotNull UnaryOperator<@NotNull Style> mergeFunction) {
        return new SymbolicStyle(symbol, mergeFunction);
    }

    public static @NotNull Collection<SymbolicStyle> legacyColors() {
        return LEGACY_COLORS;
    }

    public static @NotNull Collection<SymbolicStyle> legacyDecorations() {
        return LEGACY_DECORATIONS;
    }

    public static @NotNull Collection<SymbolicStyle> bedrockColors() {
        return BEDROCK_COLORS;
    }
}
