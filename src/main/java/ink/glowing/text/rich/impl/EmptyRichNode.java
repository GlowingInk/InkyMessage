package ink.glowing.text.rich.impl;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.rich.RichNode;
import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class EmptyRichNode implements RichNode {
    public static @NotNull EmptyRichNode emptyRichNode() {
        return Provider.PROVIDER.get();
    }

    @Override
    public @NotNull Component render(@NotNull BuildContext context) {
        return Component.empty();
    }

    private enum Provider implements InstanceProvider<EmptyRichNode> {
        PROVIDER;
        private final EmptyRichNode instance = new EmptyRichNode();

        @Override
        public @NotNull EmptyRichNode get() {
            return instance;
        }
    }
}
