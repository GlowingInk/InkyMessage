package ink.glowing.text.modifier.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static ink.glowing.text.InkyMessage.escape;
import static net.kyori.adventure.text.event.ClickEvent.*;

enum ClickModifier implements Modifier.Plain {
    INSTANCE;

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
    public @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
        var click = text.clickEvent();
        if (click != null) {
            if (text.insertion() != null) {
                return List.of(
                        asFormatted("insert", escape(text.insertion())),
                        asPreparedClick(click)
                );
            } else {
                return List.of(asPreparedClick(click));
            }
        } else if (text.insertion() != null) {
            return List.of(asFormatted("insert", escape(text.insertion())));
        }
        return List.of();
    }

    private String asPreparedClick(@NotNull ClickEvent click) {
        return asFormatted(
                switch (click.action()) {
                    case OPEN_URL -> "url";
                    case OPEN_FILE -> "file";
                    case RUN_COMMAND -> "run";
                    case SUGGEST_COMMAND -> "suggest";
                    case CHANGE_PAGE -> "page";
                    case COPY_TO_CLIPBOARD -> "copy";
                    default -> "unknown"; // Who knows what future holds
                },
                escape(click.value())
        );
    }

    @Override
    public @NotNull @LabelPattern String label() {
        return "click";
    }
}
