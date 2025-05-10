package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.UnaryOperator;

import static ink.glowing.text.InkyMessage.escape;
import static java.util.function.UnaryOperator.identity;
import static net.kyori.adventure.text.event.ClickEvent.*;

enum ClickModifier implements Modifier {
    INSTANCE;

    @Override
    public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
        String value = arguments.get(0).asString();
        return switch (arguments.parameter()) {
            case "url", "link", "open_url" ->                   asMod(openUrl(value));
            case "run", "run_command" ->                        asMod(runCommand(value));
            case "suggest", "suggest_command" ->                asMod(suggestCommand(value));
            case "copy", "clipboard", "copy_to_clipboard" ->    asMod(copyToClipboard(value));
            case "page", "change_page" ->                       asMod(changePage(value));
            case "file", "open_file" ->                         asMod(openFile(value));
            case "insert", "shift", "insertion" ->              text -> text.insertion(value);
            default -> identity();
        };
    }
    
    private static UnaryOperator<Component> asMod(ClickEvent event) {
        return text -> text.clickEvent(event);
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
