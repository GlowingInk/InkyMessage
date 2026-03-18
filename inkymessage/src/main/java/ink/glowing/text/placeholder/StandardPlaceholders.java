package ink.glowing.text.placeholder;

import ink.glowing.text.utils.Labeled;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

import static ink.glowing.text.modifier.ModifierFinder.modifierFinder;
import static ink.glowing.text.modifier.standard.StandardModifiers.*;
import static ink.glowing.text.placeholder.Placeholder.placeholder;
import static java.util.function.Function.identity;
import static net.kyori.adventure.text.Component.*;

/**
 * lang, keyboard, score and selector, ATM, are included in the serializer by default - there's no need to add them.
 * But it also means that it's impossible to remove them. TODO
 */
public final class StandardPlaceholders {
    private StandardPlaceholders() {}
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
    private static final Placeholder NEWLINE = placeholder(
            "newline", Component.newline()
    );
    private static final Placeholder HEAD = placeholder( // TODO hat
            "head",
            value -> {
                try {
                    UUID uuid = UUID.fromString(value);
                    return Component.object(ObjectContents.playerHead(uuid));
                } catch (IllegalArgumentException _) {
                    return Component.object(ObjectContents.playerHead(value));
                }
            }
    );
    @SuppressWarnings("PatternValidation")
    private static final Placeholder SPRITE = placeholder(
            "sprite",
            value -> {
                String[] parts = value.split(" ", 2);
                if (parts.length == 1) {
                    String sprite = parts[0];
                    if (!Key.parseable(sprite)) return Component.empty();
                    return Component.object(ObjectContents.sprite(Key.key(sprite)));
                } else {
                    String atlas = parts[0];
                    String sprite = parts[1];
                    if (!Key.parseable(atlas) || !Key.parseable(sprite)) return Component.empty();
                    return Component.object(ObjectContents.sprite(Key.key(atlas), Key.key(sprite)));
                }
            }
    );

    private static final Set<Placeholder> REQUIRED = Set.of(
            LANG, KEYBIND, SCORE, SELECTOR, HEAD, SPRITE
    );
    private static final Map<String, Placeholder> REQUIRED_MAP = Collections.unmodifiableMap(
            REQUIRED.stream().collect(Collectors.toMap(Labeled::label, identity()))
    );

    private static final Set<Placeholder> STANDARD = Set.of(NEWLINE);
    private static final Map<String, Placeholder> STANDARD_MAP = Collections.unmodifiableMap(
            STANDARD.stream().collect(Collectors.toMap(Labeled::label, identity()))
    );

    public static @NotNull @Unmodifiable Collection<@NotNull Placeholder> requiredPlaceholders() {
        return REQUIRED;
    }

    public static @NotNull @Unmodifiable Map<String, @NotNull Placeholder> requiredPlaceholdersMap() {
        return REQUIRED_MAP;
    }

    public static @NotNull @Unmodifiable Collection<@NotNull Placeholder> standardPlaceholders() {
        return STANDARD;
    }

    public static @NotNull @Unmodifiable Map<String, @NotNull Placeholder> standardPlaceholdersMap() {
        return STANDARD_MAP;
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

    public static @NotNull Placeholder newlinePlaceholder() {
        return NEWLINE;
    }

    public static @NotNull Placeholder headPlaceholder() {
        return HEAD;
    }

    public static @NotNull Placeholder spritePlaceholder() {
        return SPRITE;
    }
}
