package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.UnaryOperator;

import static net.kyori.adventure.text.event.HoverEvent.showText;

enum HoverModifier implements Modifier {
    INSTANCE;

    @Override
    public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
        var hoverEvent = showText(arguments.get(0).asComponent()); // TODO Others
        return text -> text.hoverEvent(hoverEvent);
    }

    @Override
    public @NotNull ArgumentValue.Type unknownArgumentType(@NotNull String parameter) {
        return ArgumentValue.Type.COMPONENT;
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
