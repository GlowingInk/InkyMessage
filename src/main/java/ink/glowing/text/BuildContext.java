package ink.glowing.text;

import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class BuildContext {
    private final InkyMessage.Resolver resolver;
    private Style lastStyle;

    BuildContext(@NotNull InkyMessage.Resolver resolver) {
        this.resolver = resolver;
        this.lastStyle = Style.empty();
    }

    public @NotNull BuildContext colorlessCopy() {
        return new BuildContext(resolver);
    }

    public @NotNull Style lastStyle() {
        return lastStyle;
    }

    public void lastStyle(@NotNull Style lastStyle) {
        this.lastStyle = lastStyle;
    }

    public @NotNull InkyMessage.Resolver resolver() {
        return resolver;
    }
}
