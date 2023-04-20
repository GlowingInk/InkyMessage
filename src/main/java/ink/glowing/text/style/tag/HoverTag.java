package ink.glowing.text.style.tag;

import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public class HoverTag implements StyleTag {
    public static @NotNull HoverTag hoverTag() {
        return Provider.PROVIDER.instance();
    }

    private HoverTag() {}

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        return text.hoverEvent(HoverEvent.showText(value)); // TODO Others
    }

    @Override
    public @NotNull String prefix() {
        return "hover";
    }

    private enum Provider implements InstanceProvider<HoverTag> {
        PROVIDER;
        private final HoverTag instance = new HoverTag();

        @Override
        public @NotNull HoverTag instance() {
            return instance;
        }
    }
}
