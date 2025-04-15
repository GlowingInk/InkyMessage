package ink.glowing.text.placeholder;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

import static ink.glowing.text.modifier.ModifierGetter.modifierGetter;
import static ink.glowing.text.placeholder.Placeholder.placeholder;
import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.TranslationArgument.component;

/**
 * Those, ATM, are included in the Resolver by default - there's no need to add them.
 * But it also means that it's impossible to remove them. TODO
 */
public final class StandardPlaceholders { private StandardPlaceholders() {}
    private static final Placeholder LANG = placeholder(
            "lang",
            value -> translatable(value),
            modifierGetter(Lang.ArgModifier.INSTANCE, Lang.FallbackModifier.INSTANCE)
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
            Selector.SeparatorModifier.INSTANCE
    );

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

    public static class Lang { private Lang() {}
        public static @NotNull Modifier.Complex argModifier() {
            return ArgModifier.INSTANCE;
        }

        public static @NotNull Modifier.Plain fallbackModifier() {
            return FallbackModifier.INSTANCE;
        }

        private static class ArgModifier implements Modifier.Complex { private ArgModifier() {}
            private static final TranslationArgument EMPTY_ARG = component(empty());
            private static final ArgModifier INSTANCE = new ArgModifier();

            @Override
            public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
                if (text instanceof TranslatableComponent lang) {
                    var args = new ArrayList<>(lang.arguments());
                    if (!param.isEmpty()) {
                        int place;
                        try {
                            place = Integer.parseInt(param) - 1;
                        } catch (NumberFormatException ignored) {
                            return text;
                        }
                        if (place < 0) return text;
                        if (args.size() > place) {
                            args.set(place, component(value));
                            return lang.arguments(args);
                        }
                        if (place != args.size()) {
                            while (place > args.size()) {
                                args.add(EMPTY_ARG);
                            }
                        }
                    }

                    args.add(component(value)); // TODO Other types
                    return lang.arguments(args);
                }
                return text;
            }

            @Override
            public @NotNull @Unmodifiable List<String> read(InkyMessage.@NotNull Resolver resolver, @NotNull Component text) {
                if (text instanceof TranslatableComponent lang) {
                    List<String> argsStr = new ArrayList<>(0);
                    for (var arg : lang.arguments()) {
                        argsStr.add(asFormatted("", arg.asComponent(), resolver));
                    }
                    return argsStr;
                }
                return List.of();
            }

            @Override
            public @NotNull @NamePattern String name() {
                return "arg";
            }
        }

        private static class FallbackModifier implements Modifier.Plain { private FallbackModifier() {}
            private static final FallbackModifier INSTANCE = new FallbackModifier();

            @Override
            public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
                return text instanceof TranslatableComponent lang
                        ? lang.fallback(value)
                        : text;
            }

            @Override
            public @NotNull @Unmodifiable List<String> read(InkyMessage.@NotNull Resolver resolver, @NotNull Component text) {
                return text instanceof TranslatableComponent lang && lang.fallback() != null
                        ? List.of(asFormatted("", lang.fallback()))
                        : List.of();
            }

            @Override
            public @NotNull @NamePattern String name() {
                return "fallback";
            }
        }
    }

    public static final class Selector { private Selector() {}
        public static @NotNull Modifier.Complex separatorModifier() {
            return SeparatorModifier.INSTANCE;
        }

        private static class SeparatorModifier implements Modifier.Complex { private SeparatorModifier() {}
            private static final SeparatorModifier INSTANCE = new SeparatorModifier();

            @Override
            public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
                return text instanceof SelectorComponent selector
                        ? selector.separator(value)
                        : text;
            }

            @Override
            public @NotNull @Unmodifiable List<String> read(InkyMessage.@NotNull Resolver resolver, @NotNull Component text) {
                return text instanceof SelectorComponent selector && selector.separator() != null
                        ? List.of(asFormatted("", selector.separator(), resolver))
                        : List.of();
            }

            @Override
            public @NotNull @NamePattern String name() {
                return "separator";
            }
        }
    }

}
