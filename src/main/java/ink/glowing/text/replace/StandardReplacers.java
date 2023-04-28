package ink.glowing.text.replace;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static ink.glowing.text.replace.Replacer.replacer;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.openUrl;

public final class StandardReplacers {
    private static final Replacer URL = replacer(
            Pattern.compile("[hH][tT][tT][pP][sS]?://[\\w%]+(?:\\.[\\w%]+)+(?:/\\S*?(?:(?=[\\s()\\[\\].,!?])|\\S$))?", Pattern.UNICODE_CASE),
            (match) -> {
                String url = match.group();
                return text(match.group()).clickEvent(openUrl(url));
            }
    );

    public static @NotNull Replacer urlReplacer() {
        return URL;
    }
}
