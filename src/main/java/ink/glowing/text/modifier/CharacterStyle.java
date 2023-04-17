package ink.glowing.text.modifier;

import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextColor.color;

public record CharacterStyle(char symbol, @NotNull UnaryOperator<@NotNull Style> mergerFunction) {
    private static final List<CharacterStyle> LEGACY_COLORS = List.of(
            characterStyle('0', (s) ->  style(NamedTextColor.BLACK)),
            characterStyle('1', (s) ->  style(NamedTextColor.DARK_BLUE)),
            characterStyle('2', (s) ->  style(NamedTextColor.DARK_GREEN)),
            characterStyle('3', (s) ->  style(NamedTextColor.DARK_AQUA)),
            characterStyle('4', (s) ->  style(NamedTextColor.DARK_RED)),
            characterStyle('5', (s) ->  style(NamedTextColor.DARK_PURPLE)),
            characterStyle('6', (s) ->  style(NamedTextColor.GOLD)),
            characterStyle('7', (s) ->  style(NamedTextColor.GRAY)),
            characterStyle('8', (s) ->  style(NamedTextColor.DARK_GRAY)),
            characterStyle('9', (s) ->  style(NamedTextColor.BLUE)),
            characterStyle('a', (s) ->  style(NamedTextColor.GREEN)),
            characterStyle('b', (s) ->  style(NamedTextColor.AQUA)),
            characterStyle('c', (s) ->  style(NamedTextColor.RED)),
            characterStyle('d', (s) ->  style(NamedTextColor.LIGHT_PURPLE)),
            characterStyle('e', (s) ->  style(NamedTextColor.YELLOW)),
            characterStyle('f', (s) ->  style(NamedTextColor.WHITE))
    );

    private static final List<CharacterStyle> LEGACY_DECORATIONS = List.of(
            characterStyle('k', (s) ->  s.decorate(TextDecoration.OBFUSCATED)),
            characterStyle('l', (s) ->  s.decorate(TextDecoration.BOLD)),
            characterStyle('m', (s) ->  s.decorate(TextDecoration.STRIKETHROUGH)),
            characterStyle('n', (s) ->  s.decorate(TextDecoration.UNDERLINED)),
            characterStyle('o', (s) ->  s.decorate(TextDecoration.ITALIC)),
            characterStyle('r', (s) ->  Style.empty())
    );

    private static final List<CharacterStyle> BEDROCK_COLORS = Utils.buildList(
            LEGACY_COLORS,
            List.of(
                    characterStyle('g', (s) -> style(color(221, 214, 5))),
                    characterStyle('h', (s) -> style(color(227, 212, 209))),
                    characterStyle('i', (s) -> style(color(206, 202, 202))),
                    characterStyle('j', (s) -> style(color(68, 58, 59))),
                    characterStyle('m', (s) -> style(color(151, 22, 7))),
                    characterStyle('n', (s) -> style(color(190, 104, 77))),
                    characterStyle('p', (s) -> style(color(222, 177, 45))),
                    characterStyle('q', (s) -> style(color(17, 160, 54))),
                    characterStyle('s', (s) -> style(color(44, 186, 168))),
                    characterStyle('t', (s) -> style(color(33, 73, 123))),
                    characterStyle('u', (s) -> style(color(154, 92, 198)))
            )
    );
    
    public static @NotNull CharacterStyle characterStyle(char symbol, @NotNull UnaryOperator<@NotNull Style> mergeFunction) {
        return new CharacterStyle(symbol, mergeFunction);
    }

    public static @NotNull Collection<CharacterStyle> legacyColors() {
        return LEGACY_COLORS;
    }

    public static @NotNull Collection<CharacterStyle> legacyDecorations() {
        return LEGACY_DECORATIONS;
    }

    public static @NotNull Collection<CharacterStyle> bedrockColors() {
        return BEDROCK_COLORS;
    }
}
