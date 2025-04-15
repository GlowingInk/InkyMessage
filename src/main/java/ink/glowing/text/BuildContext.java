package ink.glowing.text;

import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.placeholder.PlaceholderGetter;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
final class BuildContext implements PlaceholderGetter {
    private final InkyMessage.Resolver resolver;
    private final PlaceholderGetter placeholderGetter;
    private Style lastStyle;

    public BuildContext(@NotNull InkyMessage.Resolver resolver) {
        this.resolver = resolver;
        this.placeholderGetter = resolver;
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

    @Override
    public @Nullable Placeholder findPlaceholder(@NotNull String name) {
        return placeholderGetter.findPlaceholder(name);
    }
}
