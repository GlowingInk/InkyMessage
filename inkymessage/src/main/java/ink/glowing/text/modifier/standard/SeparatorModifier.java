package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.SelectorComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.UnaryOperator;

enum SeparatorModifier implements Modifier {
    INSTANCE;

    @Override
    public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
        var separator = arguments.get(0).asComponent();
        return text -> text instanceof SelectorComponent selector
                ? selector.separator(separator)
                : text;
    }

    @Override
    public @NotNull ArgumentValue.Type unknownArgumentType(@NotNull String parameter) {
        return ArgumentValue.Type.COMPONENT;
    }

    @Override
    public @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
        return text instanceof SelectorComponent selector && selector.separator() != null
                ? List.of(asFormatted("", selector.separator(), inkyMessage))
                : List.of();
    }

    @Override
    public @NotNull @LabelPattern String label() {
        return "separator";
    }
}