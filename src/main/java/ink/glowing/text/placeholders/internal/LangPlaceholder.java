package ink.glowing.text.placeholders.internal;

import ink.glowing.text.InkyMessage;
import ink.glowing.text.placeholders.Placeholder;
import ink.glowing.text.style.tag.StyleTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

import static ink.glowing.text.InkyMessage.escape;

@ApiStatus.Internal
public final class LangPlaceholder implements Placeholder {
    private static final LangPlaceholder INSTANCE = new LangPlaceholder();
    public static @NotNull LangPlaceholder langPlaceholder() {
        return INSTANCE;
    }

    private LangPlaceholder() {}

    @Override
    public @NotNull Component parse(@NotNull String value) {
        return Component.translatable(value);
    }

    @Override
    public @Nullable StyleTag.Complex getLocalTag(@NotNull String tagName) {
        return tagName.equals("lang") ? Tag.langTag() : null;
    }

    public @NotNull StyleTag.Complex tag() {
        return Tag.langTag();
    }

    @Override
    public @NotNull String name() {
        return "lang";
    }

    private static final class Tag implements StyleTag.Complex {
        private static final Tag INSTANCE = new Tag();
        private Tag() {}

        static @NotNull LangPlaceholder.Tag langTag() {
            return INSTANCE;
        }

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
}
