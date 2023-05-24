package ink.glowing.text.style.modifier.internal;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.style.modifier.StyleModifier;
import ink.glowing.text.utils.name.NamePattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public final class LangModifiers {
    private LangModifiers() {}

    @ApiStatus.Internal
    public static @NotNull StyleModifier.Complex argModifier() {
        return ArgModifier.INSTANCE;
    }

    @ApiStatus.Internal
    public static @NotNull StyleModifier.Plain fallbackModifier() {
        return FallbackModifier.INSTANCE;
    }

    private static class ArgModifier implements StyleModifier.Complex {
        private static final ArgModifier INSTANCE = new ArgModifier();
        private ArgModifier() {}

        @Override
        public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
            if (text instanceof TranslatableComponent lang) {
                var args = new ArrayList<>(lang.args());
                args.add(value);
                return lang.args(args);
            }
            return text;
        }

        @Override
        public @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text) {
            if (text instanceof TranslatableComponent lang) {
                List<String> argsStr = new ArrayList<>(1);
                for (var arg : lang.args()) {
                    argsStr.add(asFormatted("", arg, resolver));
                }
                return argsStr;
            }
            return List.of();
        }

        @Override
        public @NamePattern @NotNull String name() {
            return "arg";
        }
    }

    private static class FallbackModifier implements StyleModifier.Plain {
        private static final FallbackModifier INSTANCE = new FallbackModifier();
        private FallbackModifier() {}

        @Override
        public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
            return text instanceof TranslatableComponent lang
                    ? lang.fallback(value)
                    : text;
        }

        @Override
        public @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text) {
            return text instanceof TranslatableComponent lang && lang.fallback() != null
                    ? List.of(asFormatted("", lang.fallback()))
                    : List.of();
        }

        @Override
        public @NamePattern @NotNull String name() {
            return "fallback";
        }
    }
}
