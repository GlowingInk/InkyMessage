package ink.glowing.text.style.tag;

import ink.glowing.text.utils.InstanceProvider;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

public class ClickTag implements StyleTag {
    public static @NotNull ClickTag clickTag() {
        return Provider.PROVIDER.instance();
    }

    private ClickTag() {}

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        String plainValue = Utils.plain(value);
        return switch (param) {
            case "url", "link" -> text.clickEvent(ClickEvent.openUrl(plainValue));
            case "run" -> text.clickEvent(ClickEvent.runCommand(plainValue));
            case "suggest" -> text.clickEvent(ClickEvent.suggestCommand(plainValue));
            case "copy", "clipboard" -> text.clickEvent(ClickEvent.copyToClipboard(plainValue));
            case "page" -> text.clickEvent(ClickEvent.changePage(plainValue));
            case "file" -> text.clickEvent(ClickEvent.openFile(plainValue));
            case "insert", "shift" -> text.insertion(plainValue);
            default -> text;
        };
    }

    @Override
    public @NotNull String prefix() {
        return "click";
    }

    private enum Provider implements InstanceProvider<ClickTag> {
        PROVIDER;
        private final ClickTag instance = new ClickTag();

        @Override
        public @NotNull ClickTag instance() {
            return instance;
        }
    }
}
