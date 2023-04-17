package ink.glowing.text.rich;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.rich.impl.ComplexRichText;
import ink.glowing.text.rich.impl.EmptyRichText;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public interface RichText {
    default @NotNull Component render(@NotNull GlobalContext context) {
        return render(context, (c) -> {});
    }

    @NotNull Component render(@NotNull GlobalContext context, @NotNull Consumer<Component> output);

    static @NotNull RichText empty() {
        return EmptyRichText.emptyRichText();
    }

    static @NotNull RichText richText(@NotNull String text, @NotNull List<Modifier.Prepared> modifiers) {
        if (text.isEmpty()) return empty();
        return new ComplexRichText(text, modifiers);
    }
}
