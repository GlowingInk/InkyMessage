package ink.glowing.text;

import ink.glowing.text.placeholders.Placeholder;
import ink.glowing.text.placeholders.PlaceholderGetter;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ink.glowing.text.placeholders.Placeholder.placeholder;
import static ink.glowing.text.style.tag.internal.LangTag.langTag;
import static net.kyori.adventure.text.Component.translatable;

public final class BuildContext implements PlaceholderGetter {
    private static final Placeholder LANG_PH = placeholder(
            "lang", (value) -> translatable(value), langTag()
    );

    private final InkyMessage.Resolver resolver;
    private final PlaceholderGetter placeholderGetter;
    private Style lastStyle;

    public BuildContext(@NotNull InkyMessage.Resolver resolver) {
        this.resolver = resolver;
        this.placeholderGetter = LANG_PH;
        this.lastStyle = Style.empty();
    }

    public BuildContext(@NotNull InkyMessage.Resolver resolver, @NotNull PlaceholderGetter placeholderGetter) {
        this.resolver = resolver;
        this.placeholderGetter = LANG_PH.composePlaceholder(resolver()).composePlaceholder(placeholderGetter);
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
