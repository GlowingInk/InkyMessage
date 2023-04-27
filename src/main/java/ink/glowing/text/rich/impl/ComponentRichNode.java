package ink.glowing.text.rich.impl;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.rich.RichNode;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public record ComponentRichNode(@NotNull Component component) implements RichNode {
    @Override
    public @NotNull Component render(@NotNull BuildContext context) {
        return component;
    }
}
