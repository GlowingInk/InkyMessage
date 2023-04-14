package ink.glowing.text.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Utils {
    public static final char SECTION_CHAR = '§';
    public static final String SECTION = String.valueOf(SECTION_CHAR);

    private static final Predicate<String> HEX = Pattern.compile("#[0-9a-f]{1,6}").asMatchPredicate();

    public static @Nullable Style mergeLegacyStyle(char ch, @NotNull Style current) {
        return switch (ch) {
            case '0' -> Style.style(NamedTextColor.BLACK);
            case '1' -> Style.style(NamedTextColor.DARK_BLUE);
            case '2' -> Style.style(NamedTextColor.DARK_GREEN);
            case '3' -> Style.style(NamedTextColor.DARK_AQUA);
            case '4' -> Style.style(NamedTextColor.DARK_RED);
            case '5' -> Style.style(NamedTextColor.DARK_PURPLE);
            case '6' -> Style.style(NamedTextColor.GOLD);
            case '7' -> Style.style(NamedTextColor.GRAY);
            case '8' -> Style.style(NamedTextColor.DARK_GRAY);
            case '9' -> Style.style(NamedTextColor.BLUE);
            case 'a' -> Style.style(NamedTextColor.GREEN);
            case 'b' -> Style.style(NamedTextColor.AQUA);
            case 'c' -> Style.style(NamedTextColor.RED);
            case 'd' -> Style.style(NamedTextColor.LIGHT_PURPLE);
            case 'e' -> Style.style(NamedTextColor.YELLOW);
            case 'f' -> Style.style(NamedTextColor.WHITE);
            case 'k' -> current.decorate(TextDecoration.OBFUSCATED);
            case 'l' -> current.decorate(TextDecoration.BOLD);
            case 'm' -> current.decorate(TextDecoration.STRIKETHROUGH);
            case 'n' -> current.decorate(TextDecoration.UNDERLINED);
            case 'o' -> current.decorate(TextDecoration.ITALIC);
            case 'r' -> Style.empty();
            default -> null;
        };
    }

    public static @NotNull String plain(@NotNull Component component) {
        StringBuilder builder = new StringBuilder();
        ComponentFlattener.basic().flatten(component, builder::append);
        return builder.toString();
    }

    public static boolean isHexColor(@NotNull String text) {
        return HEX.test(text);
    }

    public static boolean isEscaped(@NotNull String input, int index) {
        boolean escaped = false;
        while (--index > -1 && input.charAt(index) == '\\') escaped = !escaped;
        return escaped;
    }
}
