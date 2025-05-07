package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.TranslationArgument.component;

final class LangModifiers {
    private LangModifiers() {}

    @TestOnly
    enum ArgsModifier implements Modifier { // TODO Merge with Arg
        INSTANCE;

        @Override
        public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
            return text -> {
                if (!(text instanceof TranslatableComponent langText)) return text;
                List<TranslationArgument> translationArgs = new ArrayList<>(arguments.list().size());
                for (var arg : arguments.list()) {
                    translationArgs.add(component(arg.asComponent()));
                }
                return langText.arguments(translationArgs);
            };
        }

        @Override
        public boolean unknownArgumentAsString(@NotNull String parameter) {
            return false;
        }

        @Override
        public @NotNull String label() {
            return "args";
        }
    }

    enum ArgModifier implements Modifier.Complex {
        INSTANCE;

        private static final TranslationArgument EMPTY_ARG = component(empty());

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
        public @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
            if (text instanceof TranslatableComponent lang) {
                List<String> argsStr = new ArrayList<>(0);
                for (var arg : lang.arguments()) {
                    argsStr.add(asFormatted("", arg.asComponent(), inkyMessage));
                }
                return argsStr;
            }
            return List.of();
        }

        @Override
        public @NotNull @LabelPattern String label() {
            return "arg";
        }
    }

    enum FallbackModifier implements Modifier.Plain {
        INSTANCE;

        @Override
        public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
            return text instanceof TranslatableComponent lang
                    ? lang.fallback(value)
                    : text;
        }

        @Override
        public @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
            return text instanceof TranslatableComponent lang && lang.fallback() != null
                    ? List.of(asFormatted("", lang.fallback()))
                    : List.of();
        }

        @Override
        public @NotNull @LabelPattern String label() {
            return "fallback";
        }
    }
}
