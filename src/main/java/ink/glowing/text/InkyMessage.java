package ink.glowing.text;

import ink.glowing.text.modifier.ClickModifier;
import ink.glowing.text.modifier.ColorModifier;
import ink.glowing.text.modifier.FontModifier;
import ink.glowing.text.modifier.HoverModifier;
import ink.glowing.text.modifier.ModifiersResolver;
import ink.glowing.text.utils.Utils;
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
import java.util.regex.Pattern;

import static ink.glowing.text.utils.Utils.SECTION;

public enum InkyMessage implements ComponentSerializer<Component, Component, String> {
    INSTANCE;

    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[&\\]()]");
    private static final Pattern UNESCAPE_PATTERN = Pattern.compile("\\\\([&\\]()])");

    private static final ModifiersResolver DEFAULT_MODIFIERS = new ModifiersResolver(
            ColorModifier.INSTANCE,
            HoverModifier.INSTANCE,
            ClickModifier.INSTANCE,
            FontModifier.INSTANCE
    );

    public @NotNull Component deserialize(@NotNull String input, @NotNull ModifiersResolver modsResolver) {
        List<RichText> richTexts = new ArrayList<>();
        String oldText = input;
        String newText = parseRich(input, richTexts, modsResolver);
        while (!newText.equals(oldText)) {
            oldText = newText;
            newText = parseRich(oldText, richTexts, modsResolver);
        }
        return new RichText(newText, List.of()).render(richTexts).component().compact();
    }

    private static @NotNull String parseRich(@NotNull String input, @NotNull List<RichText> richTexts, @NotNull ModifiersResolver modsResolver) {
        int closeIndex = input.indexOf("](");
        while (Utils.isEscaped(input, closeIndex)) closeIndex = input.indexOf("](", closeIndex + 1);
        if (closeIndex == -1) return input;
        int startIndex = -1;
        for (int index = closeIndex - 1; index > 0; index--) {
            if (input.charAt(index) == '[' && input.charAt(index - 1) == '&' && !Utils.isEscaped(input, index - 1)) {
                startIndex = index - 1;
                break;
            }
        }
        if (startIndex == -1) return input;
        int modStart = closeIndex + 1;
        int modEnd = -1;
        for (int index = modStart + 1; index < input.length(); index++) {
            char ch = input.charAt(index);
            if (ch == ')' && !Utils.isEscaped(input, index)) {
                if (index + 1 == input.length() || input.charAt(index + 1) != '(') {
                    modEnd = index;
                }
            }
        }
        ++modEnd;
        richTexts.add(new RichText(
                input.substring(startIndex + 2, closeIndex),
                modsResolver.parseModifiers(input.substring(modStart, modEnd))
        ));
        return input.substring(0, startIndex) + SECTION + (richTexts.size() - 1) + SECTION + input.substring(modEnd);
    }

    public static @NotNull String escapeAll(@NotNull String text) {
        return ESCAPE_PATTERN.matcher(text).replaceAll(result -> "\\\\" + result.group());
    }

    public static @NotNull String unescapeAll(@NotNull String text) {
        return UNESCAPE_PATTERN.matcher(text).replaceAll(result -> result.group(1));
    }

    public static @NotNull String escapeSingularSlash(@NotNull String text) {
        StringBuilder builder = new StringBuilder(text.length());
        boolean escaped = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (escaped) {
                escaped = false;
                if ("&]()\\".indexOf(ch) == -1) {
                    builder.append("\\");
                }
            } else if (ch == '\\') {
                escaped = true;
            }
            builder.append(ch);
        }
        if (escaped) builder.append('\\');
        return builder.toString();
    }

    @Override
    public @NotNull Component deserialize(@NotNull String text) {
        return deserialize(text, DEFAULT_MODIFIERS);
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
}
