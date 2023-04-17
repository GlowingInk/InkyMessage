package ink.glowing.text;

import ink.glowing.text.modifier.CharacterStyle;
import ink.glowing.text.modifier.StyleResolver;
import ink.glowing.text.modifier.impl.ClickModifier;
import ink.glowing.text.modifier.impl.ColorModifier;
import ink.glowing.text.modifier.impl.DecorModifier;
import ink.glowing.text.modifier.impl.FontModifier;
import ink.glowing.text.modifier.impl.HoverModifier;
import ink.glowing.text.rich.GlobalContext;
import ink.glowing.text.rich.RichText;
import ink.glowing.text.utils.InstanceProvider;
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
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ink.glowing.text.rich.RichText.richText;
import static ink.glowing.text.utils.Utils.SECTION;

public class InkyMessage implements ComponentSerializer<Component, Component, String> {
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[&\\]()]");
    private static final Pattern UNESCAPE_PATTERN = Pattern.compile("\\\\([&\\]()\\\\])");

    private static final StyleResolver DEFAULT_RESOLVER = StyleResolver.styleResolver()
            .modifiers(Arrays.asList(
                    ColorModifier.colorModifier(),
                    HoverModifier.hoverModifier(),
                    ClickModifier.clickModifier(),
                    FontModifier.fontModifier(),
                    DecorModifier.decorModifier()))
            .addCharacterStyles(CharacterStyle.legacyColors())
            .addCharacterStyles(CharacterStyle.legacyDecorations())
            .build();

    public static @NotNull InkyMessage inkyMessage() {
        return Provider.PROVIDER.instance();
    }

    private InkyMessage() {}

    public @NotNull Component deserialize(@NotNull String input, @NotNull StyleResolver styleResolver) {
        if (input.indexOf('&') == -1) return Component.text(input);
        List<RichText> richTexts = new ArrayList<>();
        String oldText = input;
        String newText = parseRich(input, richTexts, styleResolver);
        while (!newText.equals(oldText)) {
            oldText = newText;
            newText = parseRich(oldText, richTexts, styleResolver);
        }
        return richText(newText, List.of()).render(new GlobalContext(richTexts, styleResolver)).compact();
    }

    private static @NotNull String parseRich(@NotNull String input, @NotNull List<RichText> richTexts, @NotNull StyleResolver modsResolver) {
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
                    break;
                }
            }
        }
        ++modEnd;
        richTexts.add(richText(
                input.substring(startIndex + 2, closeIndex),
                modsResolver.parseModifiers(input.substring(modStart, modEnd))
        ));
        return input.substring(0, startIndex) + SECTION + (richTexts.size() - 1) + SECTION + input.substring(modEnd);
    }

    public static @NotNull String escapeAll(@NotNull String text) {
        return ESCAPE_PATTERN.matcher(text).replaceAll(result -> "\\\\" + result.group());
    }

    public static @NotNull String unescapeAll(@NotNull String text) {
        StringBuilder builder = new StringBuilder(text.length());
        Matcher matcher = UNESCAPE_PATTERN.matcher(text);
        while (matcher.find()) {
            matcher.appendReplacement(builder, "");
            builder.append(matcher.group(1));
        }
        return matcher.appendTail(builder).toString();
    }

    @Override
    public @NotNull Component deserialize(@NotNull String text) {
        return deserialize(text, DEFAULT_RESOLVER);
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
        public @NotNull InkyMessage instance() {
            return inkyMessage;
        }
    }
}
