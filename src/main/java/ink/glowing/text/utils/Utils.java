package ink.glowing.text.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApiStatus.Internal
public class Utils {
    public static final char SECTION_CHAR = '§';
    public static final String SECTION = String.valueOf(SECTION_CHAR);
    private static final Predicate<String> HEX_PREDICATE = Pattern.compile("#((?:[0-9a-f]{3}){1,2})").asMatchPredicate();
    private static final Pattern QUIRKY_HEX_PATTERN = Pattern.compile("&x" + "(?:&([0-9a-f]))".repeat(6));

    @Deprecated
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
        return HEX_PREDICATE.test(text);
    }

    public static @Nullable TextColor getHexColor(@NotNull String text, boolean quirky) {
        if (quirky) {
            Matcher matcher = QUIRKY_HEX_PATTERN.matcher(text);
            if (matcher.find()) {
                return TextColor.fromHexString("#" + matcher.replaceAll("$1$2$3$4$5$6"));
            }
        } else if (HEX_PREDICATE.test(text)) {
            return TextColor.fromCSSHexString(text);
        }
        return null;
    }

    @SafeVarargs
    public static <T> @NotNull List<T> buildList(Collection<T>... collections) {
        List<T> list = new ArrayList<>();
        for (var col : collections) list.addAll(col);
        return list;
    }
}
