package ink.glowing.text.modifier.impl;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.rich.RichText;
import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public class HoverModifier implements Modifier {
    public static @NotNull HoverModifier hoverModifier() {
        return Provider.PROVIDER.instance();
    }

    private HoverModifier() {}

    @Override
    public @NotNull Component modify(@NotNull RichText.Resulting resulting, @NotNull String param, @NotNull Component value) {
        Component text = resulting.asComponent();
        return text.hoverEvent(HoverEvent.showText(value)); // TODO Others
    }

    @Override
    public @NotNull String namespace() {
        return "hover";
    }

    private enum Provider implements InstanceProvider<HoverModifier> {
        PROVIDER;
        private final HoverModifier instance = new HoverModifier();

        @Override
        public @NotNull HoverModifier instance() {
            return instance;
        }
    }
}
