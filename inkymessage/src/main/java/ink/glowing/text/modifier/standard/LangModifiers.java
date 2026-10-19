package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.InkyMessage;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static java.util.function.UnaryOperator.identity;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.TranslationArgument.component;

final class LangModifiers {
    private LangModifiers() { }

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
        public @NotNull Modifier.ArgumentValue.Type unknownArgumentType(@NotNull String parameter) {
            return ArgumentValue.Type.COMPONENT;
        }

        @Override
        public @LabelPattern @NotNull String label() {
            return "args";
        }
    }

    enum ArgModifier implements Modifier {
        INSTANCE;

        private static final TranslationArgument EMPTY_ARG = component(empty());

        @Override
        public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
            String param = arguments.parameter();
            int place;
            if (param.isEmpty()) {
                place = -1;
            } else {
                try {
                    place = Integer.parseInt(param) - 1;
                } catch (NumberFormatException ignored) {
                    return identity();
                }
                if (place < 0) return identity();
            }
            var value = component(arguments.get(0).asComponent()); // TODO Other types
            return text -> {
                if (!(text instanceof TranslatableComponent lang)) return text;
                var args = new ArrayList<>(lang.arguments());
                if (place != -1) {
                    if (args.size() > place) {
                        args.set(place, value);
                        return lang.arguments(args);
                    }
                    while (place > args.size()) {
                        args.add(EMPTY_ARG);
                    }
                }
                args.add(value);
                return lang.arguments(args);
            };
        }

        @Override
        public @NotNull Modifier.ArgumentValue.Type unknownArgumentType(@NotNull String parameter) {
            return ArgumentValue.Type.COMPONENT;
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

    enum FallbackModifier implements Modifier {
        INSTANCE;

        @Override
        public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
            String fallback = arguments.get(0).asString();
            return text -> text instanceof TranslatableComponent lang
                    ? lang.fallback(fallback)
                    : text;
        }

        @Override
        public @NotNull @Unmodifiable List<String> readModifier(@NotNull Component text, @NotNull InkyMessage inkyMessage) {
            return text instanceof TranslatableComponent lang && lang.fallback() != null
                    ? List.of(asFormatted("", lang.fallback()))
                    : List.of();
        }

        @Override
        public @NotNull ArgumentValue.Type unknownArgumentType(@NotNull String parameter) {
            return ArgumentValue.Type.STRING;
        }

        @Override
        public @NotNull @LabelPattern String label() {
            return "fallback";
        }
    }
}
