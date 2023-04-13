package ink.glowing.adventure.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public record IncludedText(@NotNull Component component, @NotNull Style lastStyle) {
}
