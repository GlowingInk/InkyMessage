package ink.glowing.text.modifier;

import ink.glowing.text.rich.RichText;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyleResolver {
    private static final Pattern MODIFIERS_PATTERN = Pattern.compile("\\((\\w+):([\\w:]+)(?: ([^)]*))?\\)");

    private final Map<String, Modifier> modifiers;
    private final Map<Character, UnaryOperator<Style>> charStyles;

    public static @NotNull StyleResolver styleResolver(@NotNull Iterable<Modifier> modifiers, @NotNull Iterable<CharacterStyle> charStyles) {
        return new StyleResolver(modifiers, charStyles);
    }

    public static @NotNull StyleResolver.Builder styleResolver() {
        return new Builder();
    }

    private StyleResolver(@NotNull Iterable<Modifier> modifiers, @NotNull Iterable<CharacterStyle> charStyles) {
        this.modifiers = toMap(modifiers, Modifier::namespace, UnaryOperator.identity());
        this.charStyles = toMap(charStyles, CharacterStyle::symbol, CharacterStyle::mergerFunction);
    }

    private static <O, K, V> Map<K, V> toMap(Iterable<O> origin, Function<O, K> keyFunction, Function<O, V> valueFunction) {
        Map<K, V> map = new HashMap<>();
        for (O obj : origin) {
            map.put(keyFunction.apply(obj), valueFunction.apply(obj));
        }
        return map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(map);
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
                    matcher.group(3) == null ? RichText.empty() : RichText.richText(matcher.group(3), List.of())
            ));
        }
        return mods;
    }

    public @Nullable Style mergeCharacterStyle(char ch, @NotNull Style currentStyle) {
        UnaryOperator<Style> charStyle = charStyles.get(ch);
        if (charStyle == null) return null;
        return charStyle.apply(currentStyle);
    }

    public @NotNull StyleResolver.Builder toBuilder() {
        return new Builder()
                .modifiers(modifiers.values())
                .characterStyles(charStyles.entrySet().stream().map((entry) -> new CharacterStyle(entry.getKey(), entry.getValue())).toList());
    }

    public static class Builder implements AbstractBuilder<StyleResolver> {
        private List<Modifier> modifiers;
        private List<CharacterStyle> characterStyles;

        private Builder() {
            this.modifiers = new ArrayList<>();
            this.characterStyles = new ArrayList<>();
        }

        @Contract("_ -> this")
        public @NotNull Builder modifiers(@NotNull Collection<Modifier> modifiers) {
            this.modifiers = new ArrayList<>(modifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addModifier(Modifier mod) {
            this.modifiers.add(mod);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addModifiers(Modifier... mods) {
            return addModifiers(Arrays.asList(mods));
        }

        @Contract("_ -> this")
        public @NotNull Builder addModifiers(Iterable<Modifier> mods) {
            for (var mod : mods) this.modifiers.add(mod);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder characterStyles(@NotNull Collection<CharacterStyle> characterStyles) {
            this.characterStyles = new ArrayList<>(characterStyles);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addCharacterStyle(CharacterStyle charStyle) {
            this.characterStyles.add(charStyle);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder addCharacterStyles(CharacterStyle... charStyles) {
            return addCharacterStyles(Arrays.asList(charStyles));
        }

        @Contract("_ -> this")
        public @NotNull Builder addCharacterStyles(Iterable<CharacterStyle> charStyles) {
            for (var charStyle : charStyles) this.characterStyles.add(charStyle);
            return this;
        }

        @Override
        public @NotNull StyleResolver build() {
            return new StyleResolver(modifiers, characterStyles);
        }
    }
}
