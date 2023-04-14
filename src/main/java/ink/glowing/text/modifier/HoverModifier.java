package ink.glowing.text.modifier;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public enum HoverModifier implements Modifier {
    INSTANCE;

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        return text.hoverEvent(HoverEvent.showText(value)); // TODO Others
    }

    @Override
    public @NotNull String namespace() {
        return "hover";
    }
}
