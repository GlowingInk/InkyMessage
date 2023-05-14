package ink.glowing.text.style.modifier.internal;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.style.modifier.StyleModifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

import static ink.glowing.text.InkyMessage.escape;

@ApiStatus.Internal
public class LangModifier implements StyleModifier.Complex {
    private static final LangModifier INSTANCE = new LangModifier();
    public static @NotNull LangModifier langModifier() {
        return INSTANCE;
    }
    private LangModifier() {}

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull Component value) {
        if (text instanceof TranslatableComponent lang) {
            if (param.equals("arg")) {
                var args = new ArrayList<>(lang.args());
                args.add(value);
                return lang.args(args);
            } else if (param.equals("fallback")) {
                StringBuilder builder = new StringBuilder();
                ComponentFlattener.basic().flatten(value, builder::append);
                return lang.fallback(builder.toString());
            }
        }
        return text;
    }

    @Override
    public @NotNull @Unmodifiable List<String> read(@NotNull InkyMessage.Resolver resolver, @NotNull Component text) {
        if (text instanceof TranslatableComponent lang) {
            List<String> argsStr = new ArrayList<>(1);
            for (var arg : lang.args()) {
                argsStr.add(asFormatted("arg", arg, resolver));
            }
            if (lang.fallback() != null) {
                argsStr.add(asFormatted("fallback", escape(lang.fallback())));
            }
            return argsStr;
        }
        return List.of();
    }

    @Override
    public @NotNull String name() {
        return "lang";
    }
}
