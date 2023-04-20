package ink.glowing.text.style.tag;

import ink.glowing.text.utils.InstanceProvider;
import ink.glowing.text.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.format.TextColor.color;

public class ColorTag implements StyleTag {
    private static final Pattern PER_SYMBOL = Pattern.compile(".");
    private static final Map<String, NamedTextColor> NAMED_COLORS = NamedTextColor.NAMES.keyToValue();
    private static final List<TextColor> RAINBOW = List.of(
            color(0xff, 0x00, 0x00),
            color(0xff, 0x7f, 0x00),
            color(0xff, 0xff, 0x00),
            color(0x00, 0xff, 0x00),
            color(0x00, 0x00, 0xff),
            color(0x4b, 0x00, 0x82),
            color(0x94, 0x00, 0xd3)
    );

    public static @NotNull ColorTag colorTag() {
        return Provider.PROVIDER.instance();
    }

    private ColorTag() {}

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        TextColor color = getColor(param);
        if (color != null) {
            return text.color(color);
        } else if (param.equals("null")) {
            return text.color(null);
        }
        if (param.equals("gradient")) {
            return propagateGradient(text, getColors(Utils.plain(value)));
        }
        return text;
    }

    // TODO That's really not how this should be done
    private static @NotNull Component propagateGradient(@NotNull Component text, @NotNull List<TextColor> colors) {
        if (colors.isEmpty()) return text;
        if (colors.size() == 1) return text.colorIfAbsent(colors.get(0));
        int length = length(text);
        if (length <= 1) return text.colorIfAbsent(averageColor(colors));
        float[] step = {0};
        int indexedLength = length - 1;
        TextComponent.Builder builder = Component.text();
        for (Component child : text.children()) {
            if (child.color() != null) {
                builder.append(child);
                //noinspection ResultOfMethodCallIgnored
                child.replaceText((replaceConfig) -> replaceConfig.match(PER_SYMBOL).replacement((match, bld) -> {
                    step[0]++;
                    return bld;
                }));
            } else {
                builder.append(child.replaceText((replaceConfig) -> replaceConfig.match(PER_SYMBOL).replacement((match, bld) -> {
                    TextColor color = lerpColors(step[0]++ / indexedLength, colors);
                    return bld.colorIfAbsent(color);
                })));
            }
        }
        return builder.build();
    }

    private static int length(@NotNull Component text) {
        int[] length = {0};
        //noinspection ResultOfMethodCallIgnored
        text.replaceText((replaceConfig) -> replaceConfig.match(PER_SYMBOL).replacement(((match, builder) -> {
            length[0]++;
            return builder;
        })));
        return length[0];
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
        if (colors.size() == 2) return TextColor.lerp(step, colors.get(0), colors.get(1));
        int indexedSize = colors.size() - 1;
        int firstColor = (int) (step * indexedSize);
        if (firstColor == indexedSize) {
            return colors.get(indexedSize);
        }
        float stepAtFirst = ((float) firstColor) / indexedSize;
        float localStep = (step - stepAtFirst) * indexedSize;

        return TextColor.lerp(localStep, colors.get(firstColor), colors.get(firstColor + 1));
    }

    private static @NotNull List<TextColor> getColors(@NotNull String param) {
        if (param.equals("rainbow")) return RAINBOW;
        String[] split = param.split("-");
        List<TextColor> colors = new ArrayList<>(split.length);
        for (String colorStr : split) {
            TextColor color = getColor(colorStr);
            if (color != null) colors.add(color);
        }
        return colors;
    }

    private static @Nullable TextColor getColor(@NotNull String param) {
        if (param.startsWith("#") && Utils.isHexColor(param)) {
            return TextColor.fromCSSHexString(param);
        } else {
            return NAMED_COLORS.get(param);
        }
    }

    @Override
    public @NotNull String prefix() {
        return "color";
    }

    private enum Provider implements InstanceProvider<ColorTag> {
        PROVIDER;
        private final ColorTag instance = new ColorTag();

        @Override
        public @NotNull ColorTag instance() {
            return instance;
        }
    }
}
