package ink.glowing.text.style.modifier.standard;

import ink.glowing.text.style.modifier.StyleModifier;
import ink.glowing.text.utils.function.FloatFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorType;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public final class ColorModifier implements StyleModifier.Plain {
    private static final Map<String, NamedTextColor> NAMED_COLORS = NamedTextColor.NAMES.keyToValue();

    private static final ColorModifier INSTANCE = new ColorModifier();
    public static @NotNull StyleModifier.Plain colorModifier() {
        return INSTANCE;
    }
    private ColorModifier() {}

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        FloatFunction<TextColor> colorGetter;
        int indexedLength;
        boolean pastel = value.equals("pastel");
        switch (param) {
            case "spectrum", "rainbow" -> {
                int length = length(text);
                if (length <= 1) return text.color(NamedTextColor.WHITE);
                indexedLength = length;
                colorGetter = ColorModifier::colorSpectrum;
            }
            case "random", "rng" -> {
                indexedLength = 0;
                RandomGenerator rng = ThreadLocalRandom.current();
                colorGetter = (step) -> TextColor.color(rng.nextInt());
            }
            default -> {
                List<TextColor> colors = parseColors(param);
                if (colors.isEmpty()) return text;
                if (colors.size() == 1) return text.color(pastel ? pastel(colors.get(0)) : colors.get(0));
                int length = length(text);
                if (length <= 1) return text.color(pastel ? pastel(averageColor(colors)) : averageColor(colors));
                indexedLength = length - 1;
                colorGetter = lerpFunction(colors);
            }
        }
        if (pastel) {
            colorGetter = pastel(colorGetter);
        }
        TextComponent.Builder builder = text();
        applyDeep(builder, text, colorGetter, new float[]{0f}, indexedLength);
        return builder.build();
    }

    // FIXME That's really not how this should be done..
    private static void applyDeep(
            TextComponent.Builder outerBuilder,
            Component component,
            FloatFunction<TextColor> colorGetter,
            float[] step,
            int indexedLength
    ) {
        if (component.color() != null) {
            step[0] += length(component);
            outerBuilder.append(component);
            return;
        }
        TextComponent.Builder localBuilder = text().style(component.style());
        if (component instanceof TextComponent text) {
            String content = text.content();
            for (int i = 0; i < content.length(); i++) {
                localBuilder.append(text(content.charAt(i), colorGetter.apply(step[0]++ / indexedLength)));
            }
        } else {
            localBuilder.append(component
                    .children(Collections.emptyList()) // We'll process children ourselves
                    .color(colorGetter.apply(step[0]++ / indexedLength))
            );
        }
        for (var child : component.children()) {
            applyDeep(localBuilder, child, colorGetter, step, indexedLength);
        }
        outerBuilder.append(localBuilder);
    }

    private static int length(@NotNull Component text) {
        int length = 0;
        for (var child : text.iterable(ComponentIteratorType.BREADTH_FIRST, Collections.emptySet())) {
            if (child instanceof TextComponent textChild) {
                length += textChild.content().length();
            } else {
                length += 1;
            }
        }
        return length;
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

    /**
     * @see TextColor#color(HSVLike)
     */
    private static @NotNull TextColor colorSpectrum(float step) {
        float hue = step * 6;
        int sector = (int) hue;
        float remainder = hue - sector;
        return switch (sector) {
            case 0 ->   color(1f, remainder, 0f);
            case 1 ->   color((1 - remainder), 1f, 0f);
            case 2 ->   color(0f, 1f, remainder);
            case 3 ->   color(0f, (1 - remainder), 1f);
            case 4 ->   color(remainder, 0f, 1f);
            default ->  color(1f, 0f, (1 - remainder));
        };
    }

    private static @NotNull FloatFunction<TextColor> lerpFunction(@NotNull List<TextColor> colors) {
        if (colors.size() == 2) {
            TextColor start = colors.get(0);
            TextColor end = colors.get(1);
            return step -> TextColor.lerp(step, start, end);
        } else {
            int indexedSize = colors.size() - 1;
            return step -> {
                int firstColorIndex = (int) (step * indexedSize);
                if (firstColorIndex == indexedSize) {
                    return colors.get(indexedSize);
                }
                return TextColor.lerp((step * indexedSize) - firstColorIndex,
                        colors.get(firstColorIndex),
                        colors.get(firstColorIndex + 1)
                );
            };
        }
    }

    @Contract(value = "_ -> new")
    private static @NotNull FloatFunction<TextColor> pastel(@NotNull FloatFunction<TextColor> colorFunct) {
        return (step) -> pastel(colorFunct.apply(step));
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull TextColor pastel(@NotNull TextColor color) {
        return color(
                (color.red() + 255) / 2,
                (color.green() + 255) / 2,
                (color.blue() + 255) / 2
        );
    }

    private static @NotNull List<TextColor> parseColors(@NotNull String param) {
        String[] split = param.split("-");
        List<TextColor> colors = new ArrayList<>(split.length);
        for (String colorStr : split) {
            TextColor color = parseColor(colorStr);
            if (color != null) colors.add(color);
        }
        return colors;
    }

    private static @Nullable TextColor parseColor(@NotNull String param) {
        if (param.startsWith("#")) {
            return TextColor.fromCSSHexString(param);
        } else {
            return NAMED_COLORS.get(param);
        }
    }

    @Override
    public @NotNull String name() {
        return "color";
    }
}
