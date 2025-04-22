package ink.glowing.text.placeholder;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

import static ink.glowing.text.modifier.ModifierFinder.modifierFinder;
import static ink.glowing.text.modifier.standard.StandardModifiers.*;
import static ink.glowing.text.placeholder.Placeholder.placeholder;
import static net.kyori.adventure.text.Component.*;

/**
 * Those, ATM, are included in the Resolver by default - there's no need to add them.
 * But it also means that it's impossible to remove them. TODO
 */
public final class StandardPlaceholders { private StandardPlaceholders() {}
    private static final Placeholder LANG = placeholder(
            "lang",
            value -> translatable(value),
            modifierFinder(langArgModifier(), langFallbackModifier())
    );
    private static final Placeholder KEYBIND = placeholder("keybind", value -> keybind(value));
    private static final Placeholder SCORE = placeholder(
            "score",
            value -> {
                var split = value.split(" ", 2);
                return score(split[0], split[1]);
            }
    );
    private static final Placeholder SELECTOR = placeholder(
            "selector",
            value -> selector(value),
            selectorSeparatorModifier()
    );

    private static final Set<Placeholder> STANDARD_PLACEHOLDERS = Set.of(
            LANG, KEYBIND, SCORE, SELECTOR
    );

    public static @NotNull Collection<@NotNull Placeholder> standardPlaceholders() {
        return STANDARD_PLACEHOLDERS;
    }

    public static @NotNull Placeholder langPlaceholder() {
        return LANG;
    }

    public static @NotNull Placeholder keybindPlaceholder() {
        return KEYBIND;
    }

    public static @NotNull Placeholder scorePlaceholder() {
        return SCORE;
    }

    public static @NotNull Placeholder selectorPlaceholder() {
        return SELECTOR;
    }
}
