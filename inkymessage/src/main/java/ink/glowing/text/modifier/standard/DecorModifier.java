package ink.glowing.text.modifier.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

enum DecorModifier implements Modifier.Plain {
    INSTANCE;

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        TextDecoration decoration = decorByName(param);
        if (decoration == null) return text;
        return switch (value) {
            case "unset", "not_set" -> text.decoration(decoration, TextDecoration.State.NOT_SET);
            case "false", "removed" -> text.decoration(decoration, TextDecoration.State.FALSE);
            default -> text.decoration(decoration, TextDecoration.State.TRUE);
        };
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
