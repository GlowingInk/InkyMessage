package ink.glowing.text.symbolic.standard;

import ink.glowing.text.symbolic.SymbolicStyle;
import ink.glowing.text.utils.GeneralUtils;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.*;

public final class StandardSymbolicStyles {
    private StandardSymbolicStyles() {}

    private static final Map<TextDecoration, State> RESET_DECORATIONS;
    static {
        Map<TextDecoration, State> resetDecorations = new EnumMap<>(TextDecoration.class);
        for (var decoration : TextDecoration.values()) resetDecorations.put(decoration, State.NOT_SET);
        RESET_DECORATIONS = Collections.unmodifiableMap(resetDecorations);
    }

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

    private static final List<SymbolicStyle> NOTCHIAN_DECORATIONS = List.of(
            chainedDecoration('k', OBFUSCATED),
            chainedDecoration('l', BOLD),
            chainedDecoration('m', STRIKETHROUGH),
            chainedDecoration('n', UNDERLINED),
            chainedDecoration('o', ITALIC)
    );

    private static final List<SymbolicStyle> NOTCHIAN_FORMAT = GeneralUtils.concat(
            ArrayList::new, Collections::unmodifiableList,
            NOTCHIAN_COLORS, NOTCHIAN_DECORATIONS
    );

    private static final List<SymbolicStyle> BEDROCK_COLORS = List.of(
            chainedColor('0', BLACK),
            chainedColor('1', DARK_BLUE),
            chainedColor('2', DARK_GREEN),
            chainedColor('3', DARK_AQUA),
            chainedColor('4', DARK_RED),
            chainedColor('5', DARK_PURPLE),
            chainedColor('6', GOLD),
            chainedColor('7', color(0xC6C6C6)),
            chainedColor('8', DARK_GRAY),
            chainedColor('9', BLUE),
            chainedColor('a', GREEN),
            chainedColor('b', AQUA),
            chainedColor('c', RED),
            chainedColor('d', LIGHT_PURPLE),
            chainedColor('e', YELLOW),
            chainedColor('f', WHITE),
            chainedColor('g', color(0xDDD605)),
            chainedColor('h', color(0xE3D4D1)),
            chainedColor('i', color(0xCECACA)),
            chainedColor('j', color(0x443A3B)),
            chainedColor('m', color(0x971607)),
            chainedColor('n', color(0xB4684D)),
            chainedColor('p', color(0xDEB12D)),
            chainedColor('q', color(0x47A036)),
            chainedColor('s', color(0x2CBAA8)),
            chainedColor('t', color(0x21497B)),
            chainedColor('u', color(0x9A5CC6)),
            chainedColor('v', color(0xEB7114))
    );

    private static final List<SymbolicStyle> BEDROCK_DECORATIONS = List.of(
            chainedDecoration('k', OBFUSCATED),
            chainedDecoration('l', BOLD),
            chainedDecoration('o', ITALIC)
    );

    private static final List<SymbolicStyle> BEDROCK_FORMAT = GeneralUtils.concat(
            ArrayList::new, Collections::unmodifiableList,
            BEDROCK_COLORS, BEDROCK_DECORATIONS
    );

    private static @NotNull SymbolicStyle STANDARD_RESET = simpleReset('r');

    public static @NotNull SymbolicStyle standardReset() {
        return STANDARD_RESET;
    }

    public static char standardResetSymbol() {
        return 'r';
    }

    /**
     * Decorations defined by the notchian client/server
     * @return notchian decorations
     * @see StandardSymbolicStyles#chainedDecoration(char, TextDecoration)
     */
    @Contract(pure = true)
    public static @NotNull @Unmodifiable Collection<SymbolicStyle> notchianDecorations() {
        return NOTCHIAN_DECORATIONS;
    }

    /**
     * Colors defined by the notchian client/server
     * @return notchian colors
     * @see StandardSymbolicStyles#resettingColor(char, TextColor)
     */
    @Contract(pure = true)
    public static @NotNull @Unmodifiable Collection<SymbolicStyle> notchianColors() {
        return NOTCHIAN_COLORS;
    }

    /**
     * Combined decorations and colors defined by the notchian client/server
     * @see StandardSymbolicStyles#notchianColors()
     * @see StandardSymbolicStyles#notchianDecorations()
     */
    @Contract(pure = true)
    public static @NotNull @Unmodifiable Collection<SymbolicStyle> notchianFormat() {
        return NOTCHIAN_FORMAT;
    }

    /**
     * Decorations defined by the bedrock client/server
     * @return bedrock decorations
     * @see StandardSymbolicStyles#chainedDecoration(char, TextDecoration)
     */
    @Contract(pure = true)
    public static @NotNull @Unmodifiable Collection<SymbolicStyle> bedrockDecorations() {
        return BEDROCK_DECORATIONS;
    }

    /**
     * Colors defined by the bedrock client/server
     * @return bedrock colors
     * @see StandardSymbolicStyles#chainedColor(char, TextColor)
     */
    @Contract(pure = true)
    public static @NotNull @Unmodifiable Collection<SymbolicStyle> bedrockColors() {
        return BEDROCK_COLORS;
    }

    /**
     * Combined decorations and colors defined by the bedrock client/server
     * @see StandardSymbolicStyles#bedrockColors()
     * @see StandardSymbolicStyles#bedrockDecorations()
     */
    @Contract(pure = true)
    public static @NotNull @Unmodifiable Collection<SymbolicStyle> bedrockFormat() {
        return BEDROCK_FORMAT;
    }

    /**
     * Creates a symbolic style that just adds the decoration to the final style
     * @param symbol symbol of this style
     * @param decoration decoration to apply
     * @return a new chained decoration
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull SymbolicStyle chainedDecoration(char symbol, @NotNull TextDecoration decoration) {
        return new ChainedSymbolicDecoration(symbol, decoration);
    }

    /**
     * Creates a symbolic style that resets the decorations and sets the color to the final style
     * @param symbol symbol of this style
     * @param color color to apply
     * @return a new resetting color
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull SymbolicStyle resettingColor(char symbol, @NotNull TextColor color) {
        return new ResettingSymbolicColor(symbol, color);
    }

    /**
     * Creates a symbolic style that just sets the color to the final style
     * @param symbol symbol of this style
     * @param color color to apply
     * @return a new chained color
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull SymbolicStyle chainedColor(char symbol, @NotNull TextColor color) {
        return new ChainedSymbolicColor(symbol, color);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull SymbolicStyle simpleReset(char symbol) {
        return new SimpleSymbolicReset(symbol);
    }

    @Contract(pure = true)
    public static @NotNull @Unmodifiable Map<TextDecoration, State> resetDecorations() {
        return RESET_DECORATIONS;
    }
}
