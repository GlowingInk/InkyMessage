package ink.glowing.text.modifier.standard;

import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

enum RepeatModifier implements Modifier.Plain { // TODO Should be either removed or have more control over text amount
    INSTANCE;

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        int count;
        try {
            count = Integer.parseInt(param);
        } catch (NumberFormatException exception) {
            return text;
        }
        if (count < 0) return empty();
        if (count == 0) return text;
        var builder = text().append(text);
        for (int i = 0; i < count; i++) {
            builder.append(text);
        }
        return builder.build();
    }

    @Override
    public @NotNull @LabelPattern String label() {
        return "repeat";
    }
}
