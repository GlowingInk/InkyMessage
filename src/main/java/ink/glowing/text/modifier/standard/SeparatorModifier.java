package ink.glowing.text.modifier.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.SelectorComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

class SeparatorModifier implements Modifier.Complex { private SeparatorModifier() {}
    static final SeparatorModifier INSTANCE = new SeparatorModifier();

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        return text instanceof SelectorComponent selector
                ? selector.separator(value)
                : text;
    }

    @Override
    public @NotNull @Unmodifiable List<String> read(InkyMessage.@NotNull Resolver resolver, @NotNull Component text) {
        return text instanceof SelectorComponent selector && selector.separator() != null
                ? List.of(asFormatted("", selector.separator(), resolver))
                : List.of();
    }

    @Override
    public @NotNull @NamePattern String name() {
        return "separator";
    }
}