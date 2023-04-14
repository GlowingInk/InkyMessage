package ink.glowing.text.modifier.impl;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.utils.InstanceProvider;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ColorModifier implements Modifier {

    public static @NotNull ColorModifier colorModifier() {
        return Provider.PROVIDER.instance();
    }

    private final Map<String, NamedTextColor> namedColors;

    private ColorModifier() {
        namedColors = NamedTextColor.NAMES.keyToValue();
    }

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        if (param.startsWith("#") && Utils.isHexColor(param)) {
            return text.color(TextColor.fromHexString(param));
        } else {
            NamedTextColor namedColor = namedColors.get(param);
            if (namedColor != null) {
                return text.color(namedColor);
            } else if (param.equals("null")) {
                return text.color(null);
            }
        }
        return text;
    }

    @Override
    public @NotNull String namespace() {
        return "color";
    }

    private enum Provider implements InstanceProvider<ColorModifier> {
        PROVIDER;
        private final ColorModifier instance = new ColorModifier();

        @Override
        public @NotNull ColorModifier instance() {
            return instance;
        }
    }
}
