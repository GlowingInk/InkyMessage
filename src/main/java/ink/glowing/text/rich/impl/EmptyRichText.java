package ink.glowing.text.rich.impl;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.rich.RichText;
import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EmptyRichText implements RichText {
    public static @NotNull EmptyRichText emptyRichText() {
        return Provider.PROVIDER.instance();
    }

    @Override
    public @NotNull Component render(@NotNull BuildContext context, @NotNull Consumer<Resulting> output) {
        return Component.empty();
    }

    private enum Provider implements InstanceProvider<EmptyRichText> {
        PROVIDER;
        private final EmptyRichText instance = new EmptyRichText();

        @Override
        public @NotNull EmptyRichText instance() {
            return instance;
        }
    }
}
