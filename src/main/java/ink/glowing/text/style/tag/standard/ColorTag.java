package ink.glowing.text.style.tag.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.style.tag.StyleTag;
import ink.glowing.text.utils.function.FloatFunction;
import ink.glowing.text.utils.function.InstanceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

public final class ColorTag implements StyleTag.Plain {
    private static final Pattern PER_SYMBOL = Pattern.compile(".");
    private static final Pattern EVERYTHING = Pattern.compile(".*");

    private static final TextColor AVERAGE_SPECTRUM = TextColor.color(0x7F7F7F);
    private static final Map<String, NamedTextColor> NAMED_COLORS = NamedTextColor.NAMES.keyToValue();

    public static @NotNull ColorTag colorTag() {
        return Provider.PROVIDER.instance;
    }

    // FIXME That's really not how this should be done
    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        FloatFunction<TextColor> colorGetter;
        int indexedLength;
        if (param.equals("spectrum") || param.equals("rainbow")) {
            int length = length(text);
            if (length <= 1) return text.color(AVERAGE_SPECTRUM);
            indexedLength = length;
            colorGetter = ColorTag::colorSpectrum;
        } else {
            List<TextColor> colors = readColors(param);
            if (colors.isEmpty()) return text;
            if (colors.size() == 1) return text.color(colors.get(0));
            int length = length(text);
            if (length <= 1) return text.color() == null ? text.color(averageColor(colors)) : text;
            indexedLength = length - 1;
            colorGetter = colorLerp(colors);
        }
        int[] step = new int[]{0};
        TextComponent.Builder builder = text();
        applyGradient(builder, text, colorGetter, step, indexedLength);
        return builder.build();
    }

    private static void applyGradient(
            TextComponent.Builder outerBuilder,
            Component parent,
            FloatFunction<TextColor> colorGetter,
            int[] step,
            int indexedLength
    ) {
        TextComponent.Builder builder = text().style(parent.style());
        for (Component child : parent.children()) {
            var childChildren = child.children();
            if (!childChildren.isEmpty()) {
                applyGradient(builder, child, colorGetter, step, indexedLength);
            } else if (child.color() == null) {
                builder.append(child.replaceText((replaceConfig) -> replaceConfig
                        .match(PER_SYMBOL)
                        .replacement((match, bld) -> bld.color(colorGetter.apply((float) step[0]++ / indexedLength)))));
            } else {
                builder.append(child);
                //noinspection ResultOfMethodCallIgnored
                child.replaceText((replaceConfig) -> replaceConfig.match(EVERYTHING).replacement((match, bld) -> {
                    step[0] += match.group().length();
                    return empty();
                }));
            }
        }
        outerBuilder.append(builder);
    }

    private static int length(@NotNull Component text) {
        int[] length = {0};
        //noinspection ResultOfMethodCallIgnored
        text.replaceText((replaceConfig) -> replaceConfig.match(EVERYTHING).replacement(((match, builder) -> {
            length[0] += match.group().length();
            return empty();
        })));
        return length[0];
    }

    private static @NotNull TextColor averageColor(@NotNull Collection<TextColor> colors) {
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

    private static @NotNull TextColor colorSpectrum(float step) {
        return TextColor.color(HSVLike.hsvLike(step, 1f, 1f));
    }

    private static @NotNull FloatFunction<TextColor> colorLerp(@NotNull List<TextColor> colors) {
        if (colors.size() == 2) {
            TextColor start = colors.get(0);
            TextColor end = colors.get(1);
            return step -> TextColor.lerp(step, start, end);
        } else {
            int indexedSize = colors.size() - 1;
            return step -> {
                int firstColor = (int) (step * indexedSize);
                if (firstColor == indexedSize) {
                    return colors.get(indexedSize);
                }
                float stepAtFirst = (float) firstColor / indexedSize;
                float localStep = (step - stepAtFirst) * indexedSize;
                return TextColor.lerp(localStep, colors.get(firstColor), colors.get(firstColor + 1));
            };
        }
    }

    private static @NotNull List<TextColor> readColors(@NotNull String param) {
        String[] split = param.split("-");
        List<TextColor> colors = new ArrayList<>(split.length);
        for (String colorStr : split) {
            TextColor color = parseNamedColor(colorStr);
            if (color != null) colors.add(color);
        }
        return colors;
    }

    private static @Nullable TextColor parseNamedColor(@NotNull String param) {
        if (param.startsWith("#")) {
            return TextColor.fromCSSHexString(param);
        } else {
            return NAMED_COLORS.get(param);
        }
    }

    @Override
    public @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text) {
        return List.of();
    }

    @Override
    public @NotNull String namespace() {
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
