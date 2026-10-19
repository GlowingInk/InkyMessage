package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.UnaryOperator;

import static java.util.function.UnaryOperator.identity;

enum FontModifier implements Modifier {
    INSTANCE;

    @Override
    public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
        String param = arguments.parameter();
        if (!Key.parseable(param)) return identity();
        //noinspection PatternValidation
        Key font = Key.key(param);
        return text -> text.font(font);
    }

    @Override
    public @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
        return text.font() == null
                ? List.of()
                : List.of(asFormatted(text.font().asString(), ""));
    }

    @Override
    public @NotNull String asFormatted(@NotNull String param, @NotNull String value) {
        return label() + ":" + param;
    }

    @Override
    public @NotNull ArgumentValue.Type unknownArgumentType(@NotNull String parameter) {
        return ArgumentValue.Type.STRING;
    }

    @Override
    public @NotNull @LabelPattern String label() {
        return "font";
    }
}
