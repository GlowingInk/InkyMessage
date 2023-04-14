package ink.glowing.adventure;

import ink.glowing.adventure.modifier.ClickModifier;
import ink.glowing.adventure.modifier.ColorModifier;
import ink.glowing.adventure.modifier.FontModifier;
import ink.glowing.adventure.modifier.HoverModifier;
import ink.glowing.adventure.modifier.Modifier;
import ink.glowing.adventure.modifier.ModifiersResolver;
import ink.glowing.adventure.text.RichText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ink.glowing.adventure.utils.AdventureUtils.SPECIAL;
import static ink.glowing.adventure.utils.AdventureUtils.SPECIAL_CHAR;

public enum InkyMessage implements ComponentSerializer<Component, Component, String> {
    INSTANCE;

    private static final Pattern RICH_PATTERN = Pattern.compile(
            """
            (?x)
            &\\[(
              (?:[^]](?!&\\[))+
            )](
              (?!\\()|((?:\\(
                (?:[^)](?!&\\[))+
              \\))+)
            )
            """
    );
    private static final ModifiersResolver DEFAULT_MODIFIERS = new ModifiersResolver(
            ColorModifier.INSTANCE,
            HoverModifier.INSTANCE,
            ClickModifier.INSTANCE,
            FontModifier.INSTANCE
    );

    public @NotNull Component deserialize(@NotNull String text, @NotNull ModifiersResolver modsResolver) {
        text = text.replace(SPECIAL_CHAR, '&');
        List<RichText> richTexts = new ArrayList<>();
        for (Matcher matcher = RICH_PATTERN.matcher(text); matcher.find(); matcher = RICH_PATTERN.matcher(text)) {
            text = matcher.replaceAll((result) -> {
                String modifiersStr = result.group(2);
                List<Modifier.Prepared> mods = modifiersStr != null
                        ? modsResolver.parseModifiers(modifiersStr)
                        : List.of();
                richTexts.add(new RichText(result.group(1), mods));
                return SPECIAL + (richTexts.size() - 1) + SPECIAL;
            });
        }
        return new RichText(text, List.of()).render(richTexts).component().compact();
    }

    @Override
    public @NotNull Component deserialize(@NotNull String text) {
        return deserialize(text, DEFAULT_MODIFIERS);
    }

    @Override
    public @NotNull String serialize(@NotNull Component component) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
