package ink.glowing.text.style.tag;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import static ink.glowing.text.rich.RichNode.node;

public class HoverTag implements StyleTag {
    public static @NotNull HoverTag hoverTag() {
        return Provider.PROVIDER.get();
    }

    private HoverTag() {}

    @Override
    public @NotNull Component modify(@NotNull BuildContext context, @NotNull Component text, @NotNull String param, @NotNull String value) {
        return text.hoverEvent(HoverEvent.showText(node(value).render(context.colorlessCopy()))); // TODO Others
    }

    @Override
    public @NotNull String prefix() {
        return "hover";
    }

    private enum Provider implements InstanceProvider<HoverTag> {
        PROVIDER;
        private final HoverTag instance = new HoverTag();

        @Override
        public @NotNull HoverTag get() {
            return instance;
        }
    }
}
