package ink.glowing.text.style.tag.standard;

import ink.glowing.text.InkyMessageResolver;
import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.function.InstanceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class DecorTag implements StyleTag {
    public static @NotNull DecorTag decorTag() {
        return Provider.PROVIDER.get();
    }

    private DecorTag() {}

    @Override
    public @NotNull Component modify(@NotNull BuildContext context, @NotNull Component text, @NotNull String param, @NotNull String value) {
        TextDecoration decoration = decorByName(param);
        if (decoration == null) return text;
        return switch (value) {
            case "unset", "not_set" -> text.decoration(decoration, TextDecoration.State.NOT_SET);
            case "false", "removed" -> text.decoration(decoration, TextDecoration.State.FALSE);
            default -> text.decoration(decoration, TextDecoration.State.TRUE);
        };
    }

    @Override
    public @NotNull @Unmodifiable List<Prepared> read(@NotNull InkyMessageResolver resolver, @NotNull Component text) {
        return List.of(); // Decorators are handled by &
    }

    @Override
    public @NotNull String namespace() {
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

    private enum Provider implements InstanceProvider<DecorTag> {
        PROVIDER;
        private final DecorTag instance = new DecorTag();

        @Override
        public @NotNull DecorTag get() {
            return instance;
        }
    }
}