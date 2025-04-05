package ink.glowing.text.placeholders;

import org.jetbrains.annotations.NotNull;

import static ink.glowing.text.placeholders.Placeholder.placeholder;
import static ink.glowing.text.style.modifier.ModifierGetter.modifierGetter;
import static ink.glowing.text.style.modifier.internal.LangModifiers.argModifier;
import static ink.glowing.text.style.modifier.internal.LangModifiers.fallbackModifier;
import static net.kyori.adventure.text.Component.keybind;
import static net.kyori.adventure.text.Component.translatable;

public final class StandardPlaceholders { private StandardPlaceholders() {}
    private static final Placeholder LANG = placeholder(
            "lang",
            value -> translatable(value),
            modifierGetter(argModifier(), fallbackModifier())
    );
    private static final Placeholder KEYBIND = placeholder("keybind", value -> keybind(value));

    public static @NotNull Placeholder langPlaceholder() {
        return LANG;
    }

    public static @NotNull Placeholder keybindPlaceholder() {
        return KEYBIND;
    }
}
