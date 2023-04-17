package ink.glowing.text.modifier.impl;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.rich.RichText;
import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class FontModifier implements Modifier {
    public static @NotNull FontModifier fontModifier() {
        return Provider.PROVIDER.instance();
    }

    private FontModifier() {}

    @Override
    public @NotNull Component modify(@NotNull RichText.Resulting resulting, @NotNull String param, @NotNull Component value) {
        Component text = resulting.asComponent();
        //noinspection PatternValidation
        return Key.parseable(param) ? text.font(Key.key(param)) : text;
    }

    @Override
    public @NotNull String namespace() {
        return "font";
    }

    private enum Provider implements InstanceProvider<FontModifier> {
        PROVIDER;
        private final FontModifier instance = new FontModifier();

        @Override
        public @NotNull FontModifier instance() {
            return instance;
        }
    }
}
