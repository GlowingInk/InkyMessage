package ink.glowing.text.modifier.standard;

import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

final class RepeatModifier implements Modifier.Plain { // TODO Should be either removed or have more control over text amount
    static final RepeatModifier INSTANCE = new RepeatModifier();

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
    public @NotNull @NamePattern String name() {
        return "repeat";
    }
}
