package ink.glowing.text.modifier.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static ink.glowing.text.InkyMessage.escape;
import static net.kyori.adventure.text.event.ClickEvent.*;

public final class ClickModifier implements Modifier.Plain { private ClickModifier() {}
    private static final ClickModifier INSTANCE = new ClickModifier();

    public static @NotNull Modifier.Plain clickModifier() {
        return INSTANCE;
    }

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        return switch (param) {
            case "url", "link", "open_url" ->                   text.clickEvent(openUrl(value));
            case "run", "run_command" ->                        text.clickEvent(runCommand(value));
            case "suggest", "suggest_command" ->                text.clickEvent(suggestCommand(value));
            case "copy", "clipboard", "copy_to_clipboard" ->    text.clickEvent(copyToClipboard(value));
            case "page", "change_page" ->                       text.clickEvent(changePage(value));
            case "file", "open_file" ->                         text.clickEvent(openFile(value));
            case "insert", "shift", "insertion" ->              text.insertion(value);
            default -> text;
        };
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text) {
        if (text.clickEvent() != null) {
            if (text.insertion() != null) {
                return List.of(
                        asFormatted("insert", escape(text.insertion())),
                        asPreparedClick(text)
                );
            } else {
                return List.of(asPreparedClick(text));
            }
        } else if (text.insertion() != null) {
            return List.of(asFormatted("insert", escape(text.insertion())));
        }
        return List.of();
    }

    @SuppressWarnings("ConstantConditions")
    private String asPreparedClick(@NotNull Component text) {
        return asFormatted(
                switch (text.clickEvent().action()) {
                    case OPEN_URL -> "url";
                    case OPEN_FILE -> "file";
                    case RUN_COMMAND -> "run";
                    case SUGGEST_COMMAND -> "suggest";
                    case CHANGE_PAGE -> "page";
                    case COPY_TO_CLIPBOARD -> "copy";
                    default -> "unknown";
                },
                escape(text.clickEvent().value())
        );
    }

    @Override
    public @NotNull @NamePattern String name() {
        return "click";
    }
}
