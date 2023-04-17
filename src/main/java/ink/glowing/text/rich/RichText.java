package ink.glowing.text.rich;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.rich.impl.ComplexRichText;
import ink.glowing.text.rich.impl.EmptyRichText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public interface RichText {
    default @NotNull Component render(@NotNull TextContext context) {
        return render(context, (rt) -> {});
    }

    @NotNull Component render(@NotNull TextContext context, @NotNull Consumer<Resulting> output);

    static @NotNull RichText empty() {
        return EmptyRichText.emptyRichText();
    }

    static @NotNull RichText richText(@NotNull String text, @NotNull List<Modifier.Prepared> modifiers) {
        if (text.isEmpty()) return empty();
        return new ComplexRichText(text, modifiers);
    }

    final class Resulting implements ComponentLike {
        private final int length;
        private Component component;

        public Resulting(@NotNull Component component, int length) {
            this.component = component;
            this.length = length;
        }

        public void component(@NotNull Component component) {
            this.component = component;
        }

        @Override
        public @NotNull Component asComponent() {
            return component;
        }

        public int length() {
            return length;
        }
    }
}
