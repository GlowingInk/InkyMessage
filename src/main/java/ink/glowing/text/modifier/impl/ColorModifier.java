package ink.glowing.text.modifier.impl;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.rich.RichText;
import ink.glowing.text.utils.InstanceProvider;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.format.TextColor.color;

public class ColorModifier implements Modifier {
    private static final Pattern PER_SYMBOL = Pattern.compile(".");
    private static final List<TextColor> RAINBOW = List.of(
            color(0xff, 0x00, 0x00),
            color(0xff, 0x7f, 0x00),
            color(0xff, 0xff, 0x00),
            color(0x00, 0xff, 0x00),
            color(0x00, 0x00, 0xff),
            color(0x4b, 0x00, 0x82),
            color(0x94, 0x00, 0xd3)
    );

    public static @NotNull ColorModifier colorModifier() {
        return Provider.PROVIDER.instance();
    }

    private final Map<String, NamedTextColor> namedColors;

    private ColorModifier() {
        namedColors = NamedTextColor.NAMES.keyToValue();
    }

    @Override
    public @NotNull Component modify(@NotNull RichText.Resulting resulting, @NotNull String param, @NotNull Component value) {
        Component text = resulting.asComponent();
        TextColor color = getColor(param);
        if (color != null) {
            return text.color(color);
        } else if (param.equals("null")) {
            return text.color(null);
        }
        if (param.equals("gradient")) {
            return propagateGradient(text, resulting.length(), getColors(Utils.plain(value)));
        }
        return text;
    }

    private @NotNull Component propagateGradient(@NotNull Component text, int length, @NotNull List<TextColor> colors) {
        if (colors.isEmpty()) return text;
        if (colors.size() == 1) return text.colorIfAbsent(colors.get(0));
        if (length <= 1) return text.colorIfAbsent(averageColor(colors));
        float[] step = {0};
        int indexedLength = length - 1;
        if (colors.size() == 2) {
            TextColor colorStart = colors.get(0);
            TextColor colorEnd = colors.get(1);
            return text.replaceText((replaceConfig) -> replaceConfig
                    .match(PER_SYMBOL)
                    .replacement((match, builder) ->
                            builder.colorIfAbsent(TextColor.lerp(step[0]++ / indexedLength, colorStart, colorEnd))
                    ));
        } else {
            return text.replaceText((replaceConfig) -> {
                replaceConfig.match(PER_SYMBOL).replacement((match, builder) -> {
                    TextColor color = lerpColors(step[0]++ / indexedLength, colors);
                    return builder.colorIfAbsent(color);
                });
            });
        }
    }

    private static @NotNull TextColor averageColor(@NotNull List<TextColor> colors) {
        int red = 0;
        int green = 0;
        int blue = 0;
        for (var color : colors) {
            red += color.red();
            green += color.green();
            blue += color.blue();
        }
        red /= colors.size();
        green /= colors.size();
        blue /= colors.size();
        return TextColor.color(red, green, blue);
    }

    private static @NotNull TextColor lerpColors(float step, @NotNull List<TextColor> colors) {
        int indexedSize = colors.size() - 1;
        int firstColor = (int) (step * indexedSize);
        if (firstColor == indexedSize) {
            return colors.get(indexedSize);
        }
        float stepAtFirst = ((float) firstColor) / indexedSize;
        float localStep = (step - stepAtFirst) * indexedSize;

        return TextColor.lerp(localStep, colors.get(firstColor), colors.get(firstColor + 1));
    }

    private @NotNull List<TextColor> getColors(@NotNull String param) {
        if (param.equals("rainbow")) return RAINBOW;
        String[] split = param.split("-");
        List<TextColor> colors = new ArrayList<>(split.length);
        for (String colorStr : split) {
            TextColor color = getColor(colorStr);
            if (color != null) colors.add(color);
        }
        return colors;
    }

    private @Nullable TextColor getColor(@NotNull String param) {
        if (param.startsWith("#") && Utils.isHexColor(param)) {
            return TextColor.fromCSSHexString(param);
        } else {
            return namedColors.get(param);
        }
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
