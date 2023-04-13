package ink.glowing.adventure.modifier;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public enum ColorModifier implements Modifier {
    INSTANCE;

    private static final Predicate<String> HEX = Pattern.compile("#[0-9a-f]{1,6}").asMatchPredicate();
    private final Map<String, NamedTextColor> namedColors;

    ColorModifier() {
        namedColors = NamedTextColor.NAMES.keyToValue();
    }

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        if (HEX.test(param)) {
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
