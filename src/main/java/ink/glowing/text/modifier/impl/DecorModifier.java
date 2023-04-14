package ink.glowing.text.modifier.impl;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.utils.InstanceProvider;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DecorModifier implements Modifier {
    public static @NotNull DecorModifier decorModifier() {
        return Provider.PROVIDER.instance();
    }
    private final Map<String, TextDecoration> decorations;

    private DecorModifier() {
        this.decorations = TextDecoration.NAMES.keyToValue();
    }

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        TextDecoration decoration = decorations.get(param);
        if (decoration == null) return text;
        return switch (Utils.plain(value)) {
            case "unset", "not_set" -> text.decoration(decoration, TextDecoration.State.NOT_SET);
            case "false", "removed" -> text.decoration(decoration, TextDecoration.State.FALSE);
            default -> text.decoration(decoration, TextDecoration.State.TRUE);
        };
    }

    @Override
    public @NotNull String namespace() {
        return "decor";
    }

    private enum Provider implements InstanceProvider<DecorModifier> {
        PROVIDER;
        private final DecorModifier instance = new DecorModifier();

        @Override
        public @NotNull DecorModifier instance() {
            return instance;
        }
    }
}
