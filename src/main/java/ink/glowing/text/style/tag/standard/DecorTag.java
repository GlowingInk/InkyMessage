package ink.glowing.text.style.tag.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.style.tag.StyleTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public final class DecorTag implements StyleTag.Plain {
    private static final DecorTag INSTANCE = new DecorTag();
    public static @NotNull DecorTag decorTag() {
        return INSTANCE;
    }

    private DecorTag() {}

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
    public @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text) {
        var entries = text.decorations().entrySet();
        if (entries.isEmpty()) return List.of();
        List<String> tagStr = new ArrayList<>(0);
        for (var entry : entries) {
            if (entry.getValue() == TextDecoration.State.FALSE) {
                tagStr.add(asFormatted(entry.getKey().toString(), "false"));
            }
        }
        return tagStr;
    }

    @Override
    public @NotNull String name() {
        return "decor";
    }

    private static @Nullable TextDecoration decorByName(@NotNull String name) {
        return switch (name) {
            case "bold", "large", "b" ->                TextDecoration.BOLD;
            case "italic", "cursive", "i", "cur" ->     TextDecoration.ITALIC;
            case "underlined", "underline", "u" ->      TextDecoration.UNDERLINED;
            case "strikethrough", "st" ->               TextDecoration.STRIKETHROUGH;
            case "obfuscated", "obfuscate", "obf" ->    TextDecoration.OBFUSCATED;
            default -> null;
        };
    }
}
