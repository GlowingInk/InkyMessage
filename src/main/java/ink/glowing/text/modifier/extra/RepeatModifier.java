package ink.glowing.text.modifier.extra;

import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class RepeatModifier implements Modifier.Plain {
    private static final RepeatModifier INSTANCE = new RepeatModifier();

    public static @NotNull Modifier.Plain repeatModifier() {
        return INSTANCE;
    }

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        int count;
        try {
            count = Integer.parseInt(param);
        } catch (NumberFormatException exception) {
            return text;
        }
        if (count == 0) return text;
        var builder = Component.text().append(text);
        for (int i = 0; i < count; i++) {
            builder.append(text);
        }
        return builder.build();
    }

    @Override
    public @NotNull @NamePattern String name() {
        return "repeat";
    }
}
