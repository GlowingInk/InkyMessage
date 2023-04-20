package ink.glowing.text.style;

import ink.glowing.text.utils.Prefixed;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextColor.color;

public interface StyleModifier extends Prefixed {
    @NotNull Component modify(@NotNull Component text, @NotNull String param);

    static @NotNull Symbolic symbolic(char symbol, @NotNull UnaryOperator<@NotNull Style> mergerFunction) {
        return new Symbolic(symbol, mergerFunction);
    }

    record Symbolic(char symbol, @NotNull UnaryOperator<@NotNull Style> mergerFunction) {
        private static final List<Symbolic> LEGACY_COLORS = List.of(
                symbolicStyle('0', (s) ->  style(NamedTextColor.BLACK)),
                symbolicStyle('1', (s) ->  style(NamedTextColor.DARK_BLUE)),
                symbolicStyle('2', (s) ->  style(NamedTextColor.DARK_GREEN)),
                symbolicStyle('3', (s) ->  style(NamedTextColor.DARK_AQUA)),
                symbolicStyle('4', (s) ->  style(NamedTextColor.DARK_RED)),
                symbolicStyle('5', (s) ->  style(NamedTextColor.DARK_PURPLE)),
                symbolicStyle('6', (s) ->  style(NamedTextColor.GOLD)),
                symbolicStyle('7', (s) ->  style(NamedTextColor.GRAY)),
                symbolicStyle('8', (s) ->  style(NamedTextColor.DARK_GRAY)),
                symbolicStyle('9', (s) ->  style(NamedTextColor.BLUE)),
                symbolicStyle('a', (s) ->  style(NamedTextColor.GREEN)),
                symbolicStyle('b', (s) ->  style(NamedTextColor.AQUA)),
                symbolicStyle('c', (s) ->  style(NamedTextColor.RED)),
                symbolicStyle('d', (s) ->  style(NamedTextColor.LIGHT_PURPLE)),
                symbolicStyle('e', (s) ->  style(NamedTextColor.YELLOW)),
                symbolicStyle('f', (s) ->  style(NamedTextColor.WHITE))
        );

        private static final List<Symbolic> LEGACY_DECORATIONS = List.of(
                symbolicStyle('k', (s) ->  s.decorate(TextDecoration.OBFUSCATED)),
                symbolicStyle('l', (s) ->  s.decorate(TextDecoration.BOLD)),
                symbolicStyle('m', (s) ->  s.decorate(TextDecoration.STRIKETHROUGH)),
                symbolicStyle('n', (s) ->  s.decorate(TextDecoration.UNDERLINED)),
                symbolicStyle('o', (s) ->  s.decorate(TextDecoration.ITALIC)),
                symbolicStyle('r', (s) ->  Style.empty())
        );

        private static final List<Symbolic> BEDROCK_COLORS = Utils.buildList(
                LEGACY_COLORS,
                List.of(
                        symbolicStyle('g', (s) -> style(color(221, 214, 5))),
                        symbolicStyle('h', (s) -> style(color(227, 212, 209))),
                        symbolicStyle('i', (s) -> style(color(206, 202, 202))),
                        symbolicStyle('j', (s) -> style(color(68, 58, 59))),
                        symbolicStyle('m', (s) -> style(color(151, 22, 7))),
                        symbolicStyle('n', (s) -> style(color(190, 104, 77))),
                        symbolicStyle('p', (s) -> style(color(222, 177, 45))),
                        symbolicStyle('q', (s) -> style(color(17, 160, 54))),
                        symbolicStyle('s', (s) -> style(color(44, 186, 168))),
                        symbolicStyle('t', (s) -> style(color(33, 73, 123))),
                        symbolicStyle('u', (s) -> style(color(154, 92, 198)))
                )
        );

        public static @NotNull StyleModifier.Symbolic symbolicStyle(char symbol, @NotNull UnaryOperator<@NotNull Style> mergeFunction) {
            return new Symbolic(symbol, mergeFunction);
        }

        public static @NotNull Collection<Symbolic> legacyColors() {
            return LEGACY_COLORS;
        }

        public static @NotNull Collection<Symbolic> legacyDecorations() {
            return LEGACY_DECORATIONS;
        }

        public static @NotNull Collection<Symbolic> bedrockColors() {
            return BEDROCK_COLORS;
        }
    }
}
