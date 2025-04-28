package ink.glowing.text.replace;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static ink.glowing.text.replace.Replacer.replacer;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.openUrl;

public final class StandardReplacers {
    public static final Pattern URL_PATTERN = Pattern.compile(
            "[hH][tT][tT][pP][sS]?://" +                    // https://
            "((?:\\.?[\\w\\-]+)+(?::\\d{1,5})?)" +          // domain.com:80
            "([/#]\\S*?(?=[()\\[\\].,!?]?(?=\\s|$)))?",     // /params
            Pattern.UNICODE_CASE
    );

    private static final Replacer URL = replacer(
            URL_PATTERN,
            (match) -> {
                String url = match.group();
                return text(url).clickEvent(openUrl(url));
            }
    );

    public static @NotNull Replacer urlReplacer() {
        return URL;
    }
}
