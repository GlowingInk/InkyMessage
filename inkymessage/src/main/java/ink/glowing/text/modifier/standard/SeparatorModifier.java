package ink.glowing.text.modifier.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.SelectorComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

enum SeparatorModifier implements Modifier.Complex {
    INSTANCE;

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        return text instanceof SelectorComponent selector
                ? selector.separator(value)
                : text;
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