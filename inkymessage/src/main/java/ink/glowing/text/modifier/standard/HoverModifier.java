package ink.glowing.text.modifier.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static net.kyori.adventure.text.event.HoverEvent.showText;

enum HoverModifier implements Modifier.Complex {
    INSTANCE;

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        return text.hoverEvent(showText(value)); // TODO Others
    }

    @Override
    public @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
        var hoverEvent = text.hoverEvent();
        return hoverEvent == null || hoverEvent.action() != HoverEvent.Action.SHOW_TEXT
                ? List.of()
                : List.of(asFormatted("text", (Component) hoverEvent.value(), inkyMessage));
    }

    @Override
    public @NotNull @LabelPattern String label() {
        return "hover";
    }
}
