package ink.glowing.text.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AdventureUtils {
    private static final Map<String, NamedTextColor> NAMED_COLORS = NamedTextColor.NAMES.keyToValue();

    public static final Pattern HEX_PATTERN = Pattern.compile("&?(#(?:[0-9a-f]{3}){1,2})");
    public static final Pattern QUIRKY_HEX_PATTERN = Pattern.compile("&x" + "(?:&([0-9a-f]))".repeat(6));

    private AdventureUtils() {}

    public static @Nullable TextColor parseHexColor(@NotNull String text, boolean quirky) {
        if (quirky) {
            if (text.length() != 14) return null;
            Matcher matcher = QUIRKY_HEX_PATTERN.matcher(text);
            if (matcher.find()) {
                return TextColor.fromHexString("#" + matcher.replaceAll("$1$2$3$4$5$6"));
            }
        } else if (text.startsWith("&")) {
            text = text.substring(1);
        }
        return TextColor.fromCSSHexString(text);
    }

    public static @Nullable TextColor parseColor(@NotNull String param) {
        if (param.startsWith("#")) {
            return TextColor.fromCSSHexString(param);
        } else {
            return NAMED_COLORS.get(param);
        }
    }
}
