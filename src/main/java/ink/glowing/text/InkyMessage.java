package ink.glowing.text;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.style.symbolic.SymbolicStyle;
import ink.glowing.text.utils.function.InstanceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ink.glowing.text.InkyMessageResolver.standardInkyResolver;
import static ink.glowing.text.rich.RichNode.node;
import static ink.glowing.text.rich.RichNode.nodeId;

public class InkyMessage implements ComponentSerializer<Component, Component, String> {
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[&\\]()]");
    private static final Pattern UNESCAPE_PATTERN = Pattern.compile("\\\\([&\\]()\\\\])");

    public static @NotNull InkyMessage inkyMessage() {
        return Provider.PROVIDER.get();
    }

    private InkyMessage() {}

    /**
     * Convert string into adventure text component using standard resolver
     * @param inputText input string
     * @return converted text component
     * @see InkyMessageResolver#standardInkyResolver()
     */
    @Override
    public @NotNull Component deserialize(@NotNull String inputText) {
        return deserialize(inputText, standardInkyResolver());
    }

    /**
     * Convert string into adventure text component
     * @param inputText input string
     * @param inkyResolver resolver to use
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText, @NotNull InkyMessageResolver inkyResolver) {
        return deserialize(inputText, new BuildContext(new ArrayList<>(), inkyResolver));
    }

    /**
     * Convert string into adventure text component
     * @param inputText input string
     * @param context context to build with
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText, @NotNull BuildContext context) {
        String oldText = inputText;
        int minimalIndex = inputText.indexOf("](");
        String newText = parseRich(oldText, minimalIndex, context);
        while (!newText.equals(oldText)) {
            oldText = newText;
            newText = parseRich(oldText, 2, context);
        }
        return node(newText).render(context).compact();
    }

    private static @NotNull String parseRich(@NotNull String input, int fromIndex, @NotNull BuildContext context) {
        int closeIndex = input.indexOf("](", fromIndex);
        while (isEscaped(input, closeIndex)) closeIndex = input.indexOf("](", closeIndex + 1);
        if (closeIndex == -1) return input;
        int startIndex = -1;
        for (int index = closeIndex - 1; index > 0; index--) {
            if (input.charAt(index) == '[' && input.charAt(index - 1) == '&' && !isEscaped(input, index - 1)) {
                startIndex = index - 1;
                break;
            }
        }
        if (startIndex == -1) return input;
        int modStart = closeIndex + 1;
        int modEnd = -1;
        for (int index = modStart + 1; index < input.length(); index++) {
            char ch = input.charAt(index);
            if (ch == ')' && !isEscaped(input, index)) {
                if (index + 1 == input.length() || input.charAt(index + 1) != '(') {
                    modEnd = index;
                    break;
                }
            } else if (ch == '&' && index + 1 < input.length() && input.charAt(index + 1) == '[' && !isEscaped(input, index)) {
                input = parseRich(input, index, context);
            }
        }
        if (++modEnd == 0) {
            modEnd = input.length();
        }
        return input.substring(0, startIndex) +
                nodeId(context.innerNodeAdd(node(
                        input.substring(startIndex + 2, closeIndex),
                        context.inkyResolver().parseTags(input.substring(modStart, modEnd))
                ))) +
                input.substring(modEnd);
    }

    @Override
    public @NotNull String serialize(@NotNull Component text) {
        return serialize(text, standardInkyResolver());
    }

    public @NotNull String serialize(@NotNull Component text, @NotNull InkyMessageResolver resolver) {
        StringBuilder builder = new StringBuilder();
        serialize(builder, new TreeSet<>(), text, resolver, new boolean[]{false});
        return builder.toString();
    }

    private void serialize(
            @NotNull StringBuilder builder,
            final @NotNull TreeSet<SymbolicStyle> outerStyle,
            @NotNull Component text,
            @NotNull InkyMessageResolver resolver,
            boolean @NotNull [] previousStyled
    ) {
        var tags = resolver.readStyleTags(text);
        if (!tags.isEmpty()) {
            builder.append("&[");
        }
        var currentStyle = resolver.readSymbolics(text);
        if (previousStyled[0] && (currentStyle.isEmpty() || !currentStyle.first().resets())) {
            if (outerStyle.isEmpty()) {
                builder.append(resolver.symbolicReset().asFormatted());
            } else for (var symb : outerStyle) {
                builder.append(symb.asFormatted());
            }
        }
        if (currentStyle.isEmpty()) {
            previousStyled[0] = false;
        } else {
            previousStyled[0] = true;
            for (var symb : currentStyle) {
                builder.append(symb.asFormatted());
            }
        }

        builder.append(escape(asString(text)));
        var children = text.children();
        var newOuterStyle = new TreeSet<>(outerStyle);
        newOuterStyle.addAll(currentStyle);
        for (var child : children) {
            serialize(builder, newOuterStyle, child, resolver, previousStyled);
        }
        if (!tags.isEmpty()) {
            builder.append("]");
            for (var tag : tags) {
                builder.append(tag);
            }
        }
    }

    private static String asString(@NotNull Component component) {
        if (component instanceof TextComponent text) {
            return text.content();
        } else if (component instanceof TranslatableComponent translatable) {
            return "{tl:" + translatable.key() + "}"; // TODO implement; args, fallback
        } else if (component instanceof KeybindComponent keybind) {
            return "{keybind:" + keybind.keybind() + "}"; // TODO implement
        } else if (component instanceof ScoreComponent score) {
            return score.objective(); // TODO implement
        } else if (component instanceof SelectorComponent selector) {
            return selector.pattern(); // TODO implement
        } else {
            return "?";
        }
    }

    public static @NotNull String escape(@NotNull String text) {
        StringBuilder builder = new StringBuilder(text.length());
        Matcher matcher = ESCAPE_PATTERN.matcher(text);
        while (matcher.find()) {
            matcher.appendReplacement(builder, "");
            builder.append('\\').append(matcher.group());
        }
        return matcher.appendTail(builder).toString();
    }

    public static @NotNull String unescape(@NotNull String text) {
        StringBuilder builder = new StringBuilder(text.length());
        Matcher matcher = UNESCAPE_PATTERN.matcher(text);
        while (matcher.find()) {
            matcher.appendReplacement(builder, "");
            builder.append(matcher.group(1));
        }
        return matcher.appendTail(builder).toString();
    }

    public static boolean isEscaped(@NotNull String input, int index) {
        boolean escaped = false;
        while (--index > -1 && input.charAt(index) == '\\') escaped = !escaped;
        return escaped;
    }

    private enum Provider implements InstanceProvider<InkyMessage> {
        PROVIDER;
        private final InkyMessage inkyMessage = new InkyMessage();

        @Override
        public @NotNull InkyMessage get() {
            return inkyMessage;
        }
    }
}
