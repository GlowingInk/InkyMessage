package ink.glowing.text.modifier.standard;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.TranslationArgument.component;

class LangModifiers { private LangModifiers() {}
    static class ArgModifier implements Modifier.Complex { private ArgModifier() {}
        private static final TranslationArgument EMPTY_ARG = component(empty());
        static final ArgModifier INSTANCE = new ArgModifier();

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

    static class FallbackModifier implements Modifier.Plain { private FallbackModifier() {}
        static final FallbackModifier INSTANCE = new FallbackModifier();

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
