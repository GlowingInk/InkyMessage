package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static java.util.function.UnaryOperator.identity;

enum DecorModifier implements Modifier {
    INSTANCE;

    @Override
    public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
        TextDecoration decoration = decorByName(arguments.parameter());
        if (decoration == null) return identity();
        TextDecoration.State state = switch (arguments.get(0).asString()) {
            case "unset", "not_set" -> TextDecoration.State.NOT_SET;
            case "false", "removed" -> TextDecoration.State.FALSE;
            default -> TextDecoration.State.TRUE;
        };
        return text -> text.decoration(decoration, state);
    }

    @Override
    public @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
        var textDecors = text.decorations().entrySet();
        if (textDecors.isEmpty()) return List.of();
        List<String> modifierStr = new ArrayList<>(0);
        for (var entry : textDecors) {
            if (entry.getValue() == TextDecoration.State.FALSE) {
                modifierStr.add(asFormatted(entry.getKey().toString(), "false"));
            }
        }
        return modifierStr;
    }

    @Override
    public @NotNull ArgumentValue.Type unknownArgumentType(@NotNull String parameter) {
        return ArgumentValue.Type.STRING;
    }

    @Override
    public @NotNull @LabelPattern String label() {
        return "decor";
    }

    private static @Nullable TextDecoration decorByName(@NotNull String label) {
        return switch (label) {
            case "bold", "large", "b" ->                        TextDecoration.BOLD;
            case "italic", "cursive", "i", "cur" ->             TextDecoration.ITALIC;
            case "underlined", "underline", "u" ->              TextDecoration.UNDERLINED;
            case "strikethrough", "st" ->                       TextDecoration.STRIKETHROUGH;
            case "obfuscated", "obfuscate", "obf", "spoiler" -> TextDecoration.OBFUSCATED;
            default -> null;
        };
    }
}
