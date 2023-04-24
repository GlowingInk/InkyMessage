package ink.glowing.text.style.tag;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.event.ClickEvent.*;

public class ClickTag implements StyleTag {
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
