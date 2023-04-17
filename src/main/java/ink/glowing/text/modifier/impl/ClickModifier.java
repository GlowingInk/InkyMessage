package ink.glowing.text.modifier.impl;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.rich.RichText;
import ink.glowing.text.utils.InstanceProvider;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

public class ClickModifier implements Modifier {
    public static @NotNull ClickModifier clickModifier() {
        return Provider.PROVIDER.instance();
    }

    private ClickModifier() {}

    @Override
    public @NotNull Component modify(@NotNull RichText.Resulting resulting, @NotNull String param, @NotNull Component value) {
        Component text = resulting.asComponent();
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
    public @NotNull String namespace() {
        return "click";
    }

    private enum Provider implements InstanceProvider<ClickModifier> {
        PROVIDER;
        private final ClickModifier instance = new ClickModifier();

        @Override
        public @NotNull ClickModifier instance() {
            return instance;
        }
    }
}
