package ink.glowing.text.style.tag.standard;

import ink.glowing.text.InkyMessageResolver;
import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.function.InstanceProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class FontTag implements StyleTag {
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
    public @NotNull List<Prepared> read(@NotNull InkyMessageResolver resolver, @NotNull Component text) {
        return text.font() == null
                ? List.of()
                : List.of(new Prepared(this, text.font().asString(), ""));
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
