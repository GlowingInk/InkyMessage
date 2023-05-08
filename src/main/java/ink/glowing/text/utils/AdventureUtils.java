package ink.glowing.text.utils;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AdventureUtils {
    public static final Pattern HEX_PATTERN = Pattern.compile("#(?:[0-9a-f]{3}){1,2}");
    public static final Pattern QUIRKY_HEX_PATTERN = Pattern.compile("x" + "(?:[&§]([0-9a-f]))".repeat(6));

    private AdventureUtils() {}

    public static @Nullable TextColor parseHexColor(@NotNull String text, boolean quirky) {
        if (quirky) {
            if (text.length() != 13) return null;
            Matcher matcher = QUIRKY_HEX_PATTERN.matcher(text);
            return matcher.find()
                    ? TextColor.fromHexString("#" + matcher.replaceAll("$1$2$3$4$5$6"))
                    : null;
        }
        return TextColor.fromCSSHexString(text);
    }
}
