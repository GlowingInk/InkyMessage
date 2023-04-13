package ink.glowing.adventure;

import ink.glowing.adventure.modifier.ClickModifier;
import ink.glowing.adventure.modifier.ColorModifier;
import ink.glowing.adventure.modifier.HoverModifier;
import ink.glowing.adventure.modifier.Modifier;
import ink.glowing.adventure.modifier.ModifiersResolver;
import ink.glowing.adventure.text.RichText;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InkyMessage implements ComponentSerializer<Component, Component, String> {
    private static final Pattern RICH_PATTERN = Pattern.compile("""
                    (?x)
                    &\\[(
                      (?:(?!&\\[).)+
                    )](
                      (?!\\()|((?:\\(
                        (?:(?!&\\[)[^)])+
                      \\))+)
                    )
                    """);
    private static final Pattern MODIFIERS_PATTERN = Pattern.compile("\\((\\w+):(\\w+)?(?: ([^)]+))?\\)");
    private static final ModifiersResolver DEFAULT_MODIFIERS = new ModifiersResolver(
            ColorModifier.INSTANCE,
            HoverModifier.INSTANCE,
            ClickModifier.INSTANCE
    );

    public static final char SPECIAL_CHAR = 0;
    public static final String SPECIAL = String.valueOf(SPECIAL_CHAR);

    public @NotNull Component deserialize(@NotNull String text, @NotNull ModifiersResolver modResolvers) {
        List<RichText> richTexts = new ArrayList<>();
        for (Matcher matcher = RICH_PATTERN.matcher(text); matcher.find(); matcher = RICH_PATTERN.matcher(text)) {
            text = matcher.replaceAll((result) -> {
                String modifiersStr = result.group(2);
                List<Modifier.Prepared> mods = modifiersStr != null
                        ? parseModifiers(modifiersStr, modResolvers)
                        : List.of();
                richTexts.add(new RichText(result.group(1), mods));
                return SPECIAL + (richTexts.size() - 1) + SPECIAL;
            });
        }
        return new RichText(text, List.of()).render(Style.empty(), richTexts).component().compact();
    }

    @Override
    public @NotNull Component deserialize(@NotNull String text) {
        return deserialize(text, DEFAULT_MODIFIERS);
    }

    private List<Modifier.Prepared> parseModifiers(String modifiersStr, ModifiersResolver modsResolver) {
        List<Modifier.Prepared> mods = new ArrayList<>();
        Matcher matcher = MODIFIERS_PATTERN.matcher(modifiersStr);
        while (matcher.find()) {
            Modifier modifier = modsResolver.getModifier(matcher.group(1));
            if (modifier == null) continue;
            mods.add(new Modifier.Prepared(
                    modifier,
                    matcher.group(2) == null ? "" : matcher.group(2),
                    matcher.group(3) == null ? RichText.EMPTY : new RichText(matcher.group(3), List.of())
            ));
        }
        return mods;
    }

    @Override
    public @NotNull String serialize(@NotNull Component component) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
