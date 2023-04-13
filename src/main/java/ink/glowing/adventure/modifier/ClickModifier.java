package ink.glowing.adventure.modifier;

import ink.glowing.adventure.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

public enum ClickModifier implements Modifier {
    INSTANCE;

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        String plainValue = AdventureUtils.plain(value);
        return switch (param) {
            case "url", "link" -> text.clickEvent(ClickEvent.openUrl(plainValue));
            case "run" -> text.clickEvent(ClickEvent.runCommand(plainValue));
            case "suggest" -> text.clickEvent(ClickEvent.suggestCommand(plainValue));
            case "copy" -> text.clickEvent(ClickEvent.copyToClipboard(plainValue));
            case "page" -> text.clickEvent(ClickEvent.changePage(plainValue));
            case "file" -> text.clickEvent(ClickEvent.openFile(plainValue));
            default -> text;
        };
    }

    @Override
    public @NotNull String namespace() {
        return "click";
    }
}
