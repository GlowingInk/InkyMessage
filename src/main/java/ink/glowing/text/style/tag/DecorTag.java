package ink.glowing.text.style.tag;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DecorTag implements StyleTag {
    public static @NotNull DecorTag decorTag() {
        return Provider.PROVIDER.get();
    }
    private final Map<String, TextDecoration> decorations;

    private DecorTag() {
        this.decorations = TextDecoration.NAMES.keyToValue();
    }

    @Override
    public @NotNull Component modify(@NotNull BuildContext context, @NotNull Component text, @NotNull String param, @NotNull String value) {
        TextDecoration decoration = decorations.get(param);
        if (decoration == null) return text;
        return switch (value) {
            case "unset", "not_set" -> text.decoration(decoration, TextDecoration.State.NOT_SET);
            case "false", "removed" -> text.decoration(decoration, TextDecoration.State.FALSE);
            default -> text.decoration(decoration, TextDecoration.State.TRUE);
        };
    }

    @Override
    public @NotNull String prefix() {
        return "decor";
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
