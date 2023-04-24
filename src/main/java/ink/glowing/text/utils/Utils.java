package ink.glowing.text.utils;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApiStatus.Internal
public class Utils {
    public static final char SECTION_CHAR = '§';
    public static final String SECTION = String.valueOf(SECTION_CHAR);
    private static final Pattern HEX_PATTERN = Pattern.compile("&?(#(?:[0-9a-f]{3}){1,2})");
    private static final Pattern QUIRKY_HEX_PATTERN = Pattern.compile("&x" + "(?:&([0-9a-f]))".repeat(6));

    public static boolean isHexColor(@NotNull String text) {
        return HEX_PATTERN.matcher(text).matches();
    }

    public static @Nullable TextColor getHexColor(@NotNull String text, boolean quirky) {
        if (quirky) {
            Matcher matcher = QUIRKY_HEX_PATTERN.matcher(text);
            if (matcher.find()) {
                return TextColor.fromHexString("#" + matcher.replaceAll("$1$2$3$4$5$6"));
            }
        } else if (text.startsWith("&")) {
            text = text.substring(1);
        }
        return TextColor.fromCSSHexString(text);
    }

    @SafeVarargs
    public static <T> @NotNull List<T> buildList(Collection<T>... collections) {
        List<T> list = new ArrayList<>();
        for (var col : collections) list.addAll(col);
        return list;
    }

    public static @NotNull String replaceEach(@NotNull String input, @NotNull String search, @NotNull Supplier<String> replaceSupplier) {
        int lastAppend = 0;
        StringBuilder builder = new StringBuilder();
        for (int index = input.indexOf(search, lastAppend); index != -1; index = input.indexOf(search, lastAppend)) {
            builder.append(search, lastAppend, index).append(replaceSupplier.get());
            lastAppend = index + search.length();
        }
        if (lastAppend != input.length()) {
            builder.append(input, lastAppend, input.length());
        }
        return builder.toString();
    }

    public static @NotNull String nodeId(int i) {
        return SECTION + i + SECTION;
    }
}
