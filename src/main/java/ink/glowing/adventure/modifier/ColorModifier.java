package ink.glowing.adventure.modifier;

import ink.glowing.adventure.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum ColorModifier implements Modifier {
    INSTANCE;

    private final Map<String, NamedTextColor> namedColors;

    ColorModifier() {
        namedColors = NamedTextColor.NAMES.keyToValue();
    }

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        if (param.startsWith("#") && AdventureUtils.isHexColor(param)) {
            return text.color(TextColor.fromHexString(param));
        } else {
            NamedTextColor namedColor = namedColors.get(param);
            if (namedColor != null) {
                return text.color(namedColor);
            }
        }
        return text;
    }

    @Override
    public @NotNull String namespace() {
        return "color";
    }
}
