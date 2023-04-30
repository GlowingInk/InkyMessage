package ink.glowing.text.style.tag.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.function.InstanceProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public final class FontTag implements StyleTag.Plain {
    public static @NotNull FontTag fontTag() {
        return Provider.PROVIDER.instance;
    }

    private FontTag() {}

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        //noinspection PatternValidation
        return Key.parseable(param) ? text.font(Key.key(param)) : text;
    }

    @Override
    public @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text) {
        return text.font() == null
                ? List.of()
                : List.of(asFormatted(text.font().asString(), ""));
    }

    @Override
    public @NotNull String namespace() {
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
