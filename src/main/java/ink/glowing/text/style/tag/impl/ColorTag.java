package ink.glowing.text.style.tag.impl;

import ink.glowing.text.InkyMessageResolver;
import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.function.FloatFunction;
import ink.glowing.text.utils.function.InstanceProvider;
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

public final class ColorTag implements StyleTag {
    private static final Pattern PER_SYMBOL = Pattern.compile(".");
    private static final Pattern EVERYTHING = Pattern.compile(".*");
    private static final Map<String, NamedTextColor> NAMED_COLORS = NamedTextColor.NAMES.keyToValue();
    private static final List<TextColor> RAINBOW = List.of(
            color(0xFF0000),
            color(0xFF7F00),
            color(0xFFFF00),
            color(0x00FF00),
            color(0x00FFFF),
            color(0x0000FF),
            color(0x7F00FF)
    );

    public static @NotNull ColorTag colorTag() {
        return Provider.PROVIDER.get();
    }

    private ColorTag() {}

    @Override
    public @NotNull Component modify(@NotNull BuildContext context, @NotNull Component text, @NotNull String param, @NotNull String value) {
        TextColor color = getColor(param);
        if (color != null) {
            return text.color(color);
        } else if (param.equals("null")) {
            return text.color(null);
        }
        if (param.equals("gradient")) {
            return propagateGradient(text, getColors(value));
        }
        return text;
    }

    @Override
    public @NotNull List<Prepared> read(@NotNull InkyMessageResolver resolver, @NotNull Component text) {
        return List.of(); // Colors are handled by &
    }

    // TODO That's really not how this should be done
    private static @NotNull Component propagateGradient(@NotNull Component text, @NotNull List<TextColor> colors) {
        if (colors.isEmpty()) return text;
        if (colors.size() == 1) return text.color(colors.get(0));
        int length = length(text);
        if (length <= 1) return text.color(averageColor(colors));
        float[] step = {0};
        int indexedLength = length - 1;
        var colorGetter = colorGetter(colors);
        TextComponent.Builder builder = Component.text();
        for (Component child : text.children()) {
            if (child.color() == null) {
                builder.append(child.replaceText((replaceConfig) -> replaceConfig
                        .match(PER_SYMBOL)
                        .replacement((match, bld) -> bld.color(colorGetter.apply(step[0]++ / indexedLength)))));
            } else {
                builder.append(child);
                //noinspection ResultOfMethodCallIgnored
                child.replaceText((replaceConfig) -> replaceConfig.match(EVERYTHING).replacement((match, bld) -> {
                    step[0] += match.group().length();
                    return bld;
                }));
            }
        }
        return builder.build();
    }

    private static int length(@NotNull Component text) {
        int[] length = {0};
        //noinspection ResultOfMethodCallIgnored
        text.replaceText((replaceConfig) -> replaceConfig.match(EVERYTHING).replacement(((match, builder) -> {
            length[0] += match.group().length();
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

    private static @NotNull FloatFunction<TextColor> colorGetter(@NotNull List<TextColor> colors) {
        if (colors.size() == 2) {
            TextColor start = colors.get(0);
            TextColor end = colors.get(1);
            return step -> TextColor.lerp(step, start, end);
        } else {
            return step -> lerpColors(step, colors);
        }
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
        if (param.startsWith("#")) {
            return TextColor.fromCSSHexString(param);
        } else {
            return NAMED_COLORS.get(param);
        }
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
