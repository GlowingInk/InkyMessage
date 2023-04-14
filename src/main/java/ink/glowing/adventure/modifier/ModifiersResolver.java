package ink.glowing.adventure.modifier;

import ink.glowing.adventure.text.RichText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifiersResolver {
    private static final Pattern MODIFIERS_PATTERN = Pattern.compile("\\((\\w+):([\\w:]+)(?: ([^)]+))?\\)");

    private final Map<String, Modifier> modifiers;

    public ModifiersResolver(@NotNull Modifier @NotNull ... modifiers) {
        this(Arrays.asList(modifiers));
    }

    public ModifiersResolver(@NotNull Iterable<Modifier> modifiers) {
        Map<String, Modifier> mods = new HashMap<>();
        for (var mod : modifiers) {
            mods.put(mod.namespace(), mod);
        }
        this.modifiers = Collections.unmodifiableMap(mods);
    }

    public @Nullable Modifier getModifier(@NotNull String name) {
        return modifiers.get(name);
    }

    public @NotNull List<Modifier.Prepared> parseModifiers(String modifiersStr) {
        List<Modifier.Prepared> mods = new ArrayList<>();
        Matcher matcher = MODIFIERS_PATTERN.matcher(modifiersStr);
        while (matcher.find()) {
            Modifier modifier = getModifier(matcher.group(1));
            if (modifier == null) continue;
            mods.add(new Modifier.Prepared(
                    modifier,
                    matcher.group(2) == null ? "" : matcher.group(2),
                    matcher.group(3) == null ? RichText.EMPTY : new RichText(matcher.group(3), List.of())
            ));
        }
        return mods;
    }
}
