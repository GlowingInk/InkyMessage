package ink.glowing.text.style.tag;

import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class FontTag implements StyleTag {
    public static @NotNull FontTag fontTag() {
        return Provider.PROVIDER.instance();
    }

    private FontTag() {}

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        //noinspection PatternValidation
        return Key.parseable(param) ? text.font(Key.key(param)) : text;
    }

    @Override
    public @NotNull String prefix() {
        return "font";
    }

    private enum Provider implements InstanceProvider<FontTag> {
        PROVIDER;
        private final FontTag instance = new FontTag();

        @Override
        public @NotNull FontTag instance() {
            return instance;
        }
    }
}
