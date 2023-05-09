package ink.glowing.text.style.tag.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.style.tag.StyleTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static net.kyori.adventure.text.event.HoverEvent.showText;

public final class HoverTag implements StyleTag.Complex {
    private static final HoverTag INSTANCE = new HoverTag();
    public static @NotNull HoverTag hoverTag() {
        return INSTANCE;
    }
    private HoverTag() {}

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        return text.hoverEvent(showText(value)); // TODO Others
    }

    @Override
    public @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text) {
        return text.hoverEvent() == null || text.hoverEvent().action() != HoverEvent.Action.SHOW_TEXT
                ? List.of()
                : List.of(asFormatted("text", (Component) text.hoverEvent().value(), resolver));
    }

    @Override
    public @NotNull String name() {
        return "hover";
    }
}
