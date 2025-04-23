package ink.glowing.text.replace;

import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static ink.glowing.text.replace.Replacer.replacer;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.openUrl;

public final class StandardReplacers {
    private static final Pattern URL_PATTERN = Pattern.compile(
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

    private static final Replacer FANCY_URL = replacer(
            URL_PATTERN,
            (match) -> {
                String fullUrl = match.group();
                String domain = match.group(1);
                String add = match.group(2);

                if (add != null) {
                    if (add.length() > 13) {
                        add = add.substring(0, 5) + "..." + add.substring(add.length() - 5);
                    }
                } else {
                    add = "";
                }

                return text(domain + add).style((builder) -> builder
                        .decorate(TextDecoration.ITALIC)
                        .clickEvent(openUrl(fullUrl))
                        .hoverEvent(text(fullUrl))
                );
            }
    );

    public static @NotNull Replacer urlReplacer() {
        return URL;
    }

    public static @NotNull Replacer fancyUrlReplacer() {
        return FANCY_URL;
    }
}
