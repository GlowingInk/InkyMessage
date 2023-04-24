package ink.glowing.text.style.tag;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class FontTag implements StyleTag {
    public static @NotNull FontTag fontTag() {
        return Provider.PROVIDER.get();
    }

    private FontTag() {}

    @Override
    public @NotNull Component modify(@NotNull BuildContext context, @NotNull Component text, @NotNull String param, @NotNull String value) {
        //noinspection PatternValidation
        return Key.parseable(param) ? text.font(Key.key(param)) : text;
    }

    @Override
    public @NotNull String namespace() {
        return "font";
    }

    private enum Provider implements InstanceProvider<FontTag> {
        PROVIDER;
        private final FontTag instance = new FontTag();

        @Override
        public @NotNull FontTag get() {
            return instance;
        }
    }
}
