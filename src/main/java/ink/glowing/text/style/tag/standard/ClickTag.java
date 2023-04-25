package ink.glowing.text.style.tag.standard;

import ink.glowing.text.InkyMessageResolver;
import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.function.InstanceProvider;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static net.kyori.adventure.text.event.ClickEvent.*;

public final class ClickTag implements StyleTag {
    public static @NotNull ClickTag clickTag() {
        return Provider.PROVIDER.get();
    }

    private ClickTag() {}

    @Override
    public @NotNull Component modify(@NotNull BuildContext context, @NotNull Component text, @NotNull String param, @NotNull String value) {
        return switch (param) {
            case "url", "link" -> text.clickEvent(openUrl(value));
            case "run" -> text.clickEvent(runCommand(value));
            case "suggest" -> text.clickEvent(suggestCommand(value));
            case "copy", "clipboard" -> text.clickEvent(copyToClipboard(value));
            case "page" -> text.clickEvent(changePage(value));
            case "file" -> text.clickEvent(openFile(value));
            case "insert", "shift" -> text.insertion(value);
            default -> text;
        };
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull @Unmodifiable List<Prepared> read(@NotNull InkyMessageResolver resolver, @NotNull Component text) {
        if (text.clickEvent() != null) {
            if (text.insertion() != null) {
                return List.of(
                        new Prepared(this, "insert", text.insertion()),
                        asPreparedClick(text)
                );
            } else {
                return List.of(asPreparedClick(text));
            }
        } else if (text.insertion() != null) {
            return List.of(new Prepared(this, "insert", text.insertion()));
        }
        return List.of();
    }

    @SuppressWarnings("ConstantConditions")
    private Prepared asPreparedClick(@NotNull Component text) {
        return new Prepared(
                this,
                switch (text.clickEvent().action()) {
                    case OPEN_URL -> "url";
                    case OPEN_FILE -> "file";
                    case RUN_COMMAND -> "run";
                    case SUGGEST_COMMAND -> "suggest";
                    case CHANGE_PAGE -> "page";
                    case COPY_TO_CLIPBOARD -> "copy";
                    default -> "unknown";
                },
                text.clickEvent().value()
        );
    }

    @Override
    public @NotNull String namespace() {
        return "click";
    }

    private enum Provider implements InstanceProvider<ClickTag> {
        PROVIDER;
        private final ClickTag instance = new ClickTag();

        @Override
        public @NotNull ClickTag get() {
            return instance;
        }
    }
}
