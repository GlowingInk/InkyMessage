package ink.glowing.text.style.tag.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.function.InstanceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import static ink.glowing.text.style.tag.standard.GradientTag.gradientTag;
import static ink.glowing.text.utils.AdventureUtils.parseNamedColor;

public final class ColorTag implements StyleTag.Plain {
    public static @NotNull ColorTag colorTag() {
        return Provider.PROVIDER.instance;
    }

    private ColorTag() {}

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        TextColor color = parseNamedColor(param);
        if (color != null) {
            return text.color(color);
        } else if (param.equals("null")) {
            return text.color(null);
        }
        if (param.equals("gradient")) {
            return gradientTag().modify(text, value, "");
        }
        return text;
    }

    @Override
    public @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text) {
        return List.of(); // Colors are handled by &
    }

    @Override
    public @NotNull String namespace() {
        return "color";
    }

    private enum Provider implements InstanceProvider<ColorTag> {
        PROVIDER;
        private final ColorTag instance = new ColorTag();

        @Override
        public @NotNull ColorTag get() {
            return instance;
        }
    }
}
