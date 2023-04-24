package ink.glowing.text;

import ink.glowing.text.rich.BuildContext;
import ink.glowing.text.rich.RichNode;
import ink.glowing.text.utils.InstanceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorType;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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

    public @NotNull Component deserialize(@NotNull String input, @NotNull InkyMessageResolver inkyResolver) {
        List<RichNode> richNodes = new ArrayList<>();
        BuildContext context = new BuildContext(richNodes, inkyResolver);

        String oldText = input;
        int minimalIndex = input.indexOf("](");
        String newText = parseRich(oldText, minimalIndex, context);
        while (!newText.equals(oldText)) {
            oldText = newText;
            newText = parseRich(oldText, minimalIndex, context);
        }
        return node(newText).render(new BuildContext(richNodes, inkyResolver)).compact();
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
                nodeId(context.innerTextAdd(node(
                        input.substring(startIndex + 2, closeIndex),
                        context.inkyResolver().parseTags(input.substring(modStart, modEnd))
                ))) +
                input.substring(modEnd);
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

    @Override
    public @NotNull Component deserialize(@NotNull String text) {
        return deserialize(text, standardInkyResolver());
    }

    @Override
    public @NotNull String serialize(@NotNull Component component) {
        StringBuilder builder = new StringBuilder();
        for (var child : component.iterable(ComponentIteratorType.BREADTH_FIRST)) {
            serialize(builder, child);
        }
        return builder.toString();
    }

    private void serialize(@NotNull StringBuilder builder, @NotNull Component component) {
        if (component instanceof TextComponent text) {
            builder.append(text.content());
        } else if (component instanceof TranslatableComponent translatable) {
            builder.append(translatable.key());
        } else if (component instanceof KeybindComponent keybind) {
            builder.append(keybind.keybind());
        } else if (component instanceof ScoreComponent score) {
            builder.append(score.objective());
        } else if (component instanceof SelectorComponent selector) {
            builder.append(selector.pattern());
        } else {
            builder.append('?');
        }
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
