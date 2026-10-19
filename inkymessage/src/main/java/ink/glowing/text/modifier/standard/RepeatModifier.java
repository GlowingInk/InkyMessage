package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

import static java.util.function.UnaryOperator.identity;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

enum RepeatModifier implements Modifier { // TODO Should be either removed or have more control over text amount
    INSTANCE;

    @Override
    public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
        int count;
        try {
            count = Integer.parseInt(arguments.parameter());
        } catch (NumberFormatException exception) {
            return identity();
        }
        if (count < 0) return _ -> empty();
        if (count == 0) return identity();
        return text -> {
            var builder = text().append(text);
            for (int i = 0; i < count; i++) {
                builder.append(text);
            }
            return builder.build();
        };
    }

    @Override
    public @NotNull ArgumentValue.Type unknownArgumentType(@NotNull String parameter) {
        return ArgumentValue.Type.STRING;
    }

    @Override
    public @NotNull @LabelPattern String label() {
        return "repeat";
    }
}
