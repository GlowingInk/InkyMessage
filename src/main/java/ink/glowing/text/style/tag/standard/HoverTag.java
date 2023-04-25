package ink.glowing.text.style.tag.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.InkyMessageResolver;
import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.function.InstanceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ink.glowing.text.rich.RichNode.node;
import static net.kyori.adventure.text.event.HoverEvent.showText;

public final class HoverTag implements StyleTag {
    public static @NotNull HoverTag hoverTag() {
        return Provider.PROVIDER.get();
    }

    private HoverTag() {}

    @Override
    public @NotNull Component modify(@NotNull BuildContext context, @NotNull Component text, @NotNull String param, @NotNull String value) {
        return text.hoverEvent(showText(node(value).render(context.colorlessCopy()))); // TODO Others
    }

    @Override
    public @NotNull List<Prepared> read(@NotNull InkyMessageResolver resolver, @NotNull Component text) {
        return text.hoverEvent() == null || text.hoverEvent().action() != HoverEvent.Action.SHOW_TEXT
                ? List.of()
                : List.of(new Prepared(this, "text", InkyMessage.inkyMessage().serialize((Component) text.hoverEvent().value(), resolver)));
    }

    @Override
    public @NotNull String namespace() {
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
