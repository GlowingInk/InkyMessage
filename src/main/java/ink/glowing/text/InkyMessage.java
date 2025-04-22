package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierFinder;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.placeholder.PlaceholderFinder;
import ink.glowing.text.replace.ReplacementMatcher;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.symbolic.StandardSymbolicStyles;
import ink.glowing.text.symbolic.SymbolicStyle;
import ink.glowing.text.symbolic.SymbolicStyleFinder;
import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

import static ink.glowing.text.Stringifier.stringify;
import static ink.glowing.text.modifier.standard.StandardModifiers.standardModifiers;
import static ink.glowing.text.placeholder.StandardPlaceholders.standardPlaceholders;
import static ink.glowing.text.replace.ReplacementMatcher.replacementMatcher;
import static ink.glowing.text.replace.StandardReplacers.urlReplacer;
import static ink.glowing.text.symbolic.StandardSymbolicStyles.notchianFormat;
import static ink.glowing.text.symbolic.StandardSymbolicStyles.notchianResetSymbol;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableMap;

/**
 * User-friendly component (de)serializer with legacy-inspired format.
 */
public final class InkyMessage implements ComponentSerializer<Component, Component, String> {
    private static final InkyMessage STANDARD = builder()
            .addModifiers(standardModifiers())
            .addSymbolics(notchianFormat())
            .symbolicReset(notchianResetSymbol())
            .addReplacer(urlReplacer())
            .build();
    
    private final Map<String, Modifier<?>> modifiers;
    private final Map<String, Placeholder> placeholders;
    private final Map<Character, SymbolicStyle> symbolics;
    private final Collection<Replacer> replacers;
    private final SymbolicStyle symbolicReset;

    private final Resolver baseResolver;

    private InkyMessage(
            @NotNull Map<String, Modifier<?>> modifiers,
            @NotNull Map<String, Placeholder> placeholders,
            @NotNull Map<Character, SymbolicStyle> symbolics,
            @NotNull Collection<Replacer> replacers,
            @NotNull SymbolicStyle symbolicReset
    ) {
        this.modifiers = unmodifiableMap(modifiers);
        this.placeholders = unmodifiableMap(placeholders);
        this.symbolics = unmodifiableMap(symbolics);
        this.replacers = unmodifiableCollection(replacers);
        this.symbolicReset = symbolicReset;

        Map<String, Placeholder> adjustedPlaceholders = new HashMap<>(placeholders);
        for (var ph : standardPlaceholders()) {
            adjustedPlaceholders.put(ph.name(), ph);
        }
        Map<Character, SymbolicStyle> adjustedSymbolics = new HashMap<>(symbolics);
        adjustedSymbolics.put(symbolicReset.symbol(), symbolicReset);
        this.baseResolver = new ResolverImpl(
                this,
                modifiers::get,
                adjustedPlaceholders::get,
                adjustedSymbolics::get,
                replacementMatcher(replacers),
                symbolicReset
        );
    }

    /**
     * Get currently used resolver.
     * @return current resolver
     */
    public @NotNull Resolver resolver() {
        return baseResolver;
    }

    public @Unmodifiable @NotNull Map<String, Modifier<?>> modifiers() {
        return modifiers;
    }

    public @Unmodifiable @NotNull Map<String, Placeholder> placeholders() {
        return placeholders;
    }

    public @Unmodifiable @NotNull Map<Character, SymbolicStyle> symbolics() {
        return symbolics;
    }

    public @Unmodifiable @NotNull Collection<Replacer> replacers() {
        return replacers;
    }

    public @NotNull SymbolicStyle symbolicReset() {
        return symbolicReset;
    }

    /**
     * Gets the standard instance of InkyMessage.
     * @return the instance
     */
    @Contract(pure = true)
    public static @NotNull InkyMessage inkyMessage() {
        return STANDARD;
    }

    public static @NotNull InkyMessage inkyMessage(@NotNull Ink.Stable ink) {
        return builder().addInk(ink).build();
    }

    public static @NotNull InkyMessage inkyMessage(@NotNull Ink.Stable @NotNull ... inks) {
        return builder().addInks(inks).build();
    }

    public static @NotNull InkyMessage inkyMessage(@NotNull Iterable<Ink.@NotNull Stable> inks) {
        return builder().addInks(inks).build();
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @return converted text component
     */
    @Override
    public @NotNull Component deserialize(@NotNull String inputText) {
        return deserialize(inputText, this.baseResolver);
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param ink additional style ink
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Ink ink) {
        return deserialize(inputText, this.baseResolver.with(ink));
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param inks additional style inks
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Ink @NotNull ... inks) {
        return deserialize(inputText, this.baseResolver.with(inks));
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param inks additional style inks
     * @return converted text component
     */
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Iterable<@NotNull Ink> inks) {
        return deserialize(inputText, this.baseResolver.with(inks));
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param resolver resolver to use
     * @return converted text component
     */
    public static @NotNull Component deserialize(@NotNull String inputText,
                                                  @NotNull Resolver resolver) {
        return Parser.parse(inputText, new BuildContext(resolver)).compact();
    }

    /**
     * Convert adventure component into string.
     * @param text input component
     * @return converted string representation
     */
    @Override
    public @NotNull String serialize(@NotNull Component text) {
        return stringify(text, this);
    }

    /**
     * Escape special characters with slashes.
     * @param text text to escape
     * @return escaped string
     */
    public static @NotNull String escape(@NotNull String text) {
        StringBuilder builder = new StringBuilder(text.length());
        for (int index = 0; index < text.length(); index++) {
            char ch = text.charAt(index);
            if (isSpecial(ch)) {
                builder.append('\\');
            }
            builder.append(ch);
        }
        return builder.toString();
    }

    /**
     * Unescape special characters.
     * @param text text to unescape
     * @return unescaped string
     */
    public static @NotNull String unescape(@NotNull String text) {
        if (text.indexOf('\\') == -1) return text;

        StringBuilder builder = new StringBuilder(text.length());
        int length = text.length();

        for (
                int index = 0, nextIndex = text.indexOf('\\');
                index < length;
                index = nextIndex + 2, nextIndex = text.indexOf('\\', index)
        ) {
            if (nextIndex == -1 || nextIndex + 1 >= length) {
                builder.append(text, index, length);
                break;
            }
            builder.append(text, index, nextIndex);
            char nextCh = text.charAt(nextIndex + 1);
            if (isNotSpecial(nextCh)) builder.append('\\');
            builder.append(nextCh);
        }

        return builder.toString();
    }

    /**
     * Check if character is escaped.
     * @param input text to check in
     * @param index index of character to check
     * @return is character escaped
     */
    public static boolean isEscapedAt(@NotNull String input, int index) {
        boolean escaped = false;
        while (--index > -1 && input.charAt(index) == '\\') escaped = !escaped;
        return escaped;
    }

    /**
     * Check if character is not escaped.
     * @param input text to check in
     * @param index index of character to check
     * @return is character unescaped
     */
    public static boolean isUnescapedAt(@NotNull String input, int index) {
        return !isEscapedAt(input, index);
    }

    /**
     * Check if character is special and should be escaped.
     * @param ch character to check
     * @return is character escapable
     */
    public static boolean isSpecial(char ch) {
        return switch (ch) {
            case '&', '[', ']', '(', ')', '{', '}', '\\' -> true;
            default -> false;
        };
    }

    /**
     * Check if character is not special.
     * @param ch character to check
     * @return is character unescapable
     */
    public static boolean isNotSpecial(char ch) {
        return !isSpecial(ch);
    }

    /**
     * Creates a new resolver builder.
     * @return a builder
     */
    public static @NotNull InkyMessage.Builder builder() {
        return new Builder();
    }

    public @NotNull InkyMessage.Builder toBuilder() {
        return new InkyMessage.Builder(
                new HashMap<>(modifiers),
                new HashMap<>(placeholders),
                new HashMap<>(symbolics),
                new HashSet<>(replacers),
                symbolicReset
        );
    }

    public interface Resolver extends ModifierFinder, PlaceholderFinder, SymbolicStyleFinder, ReplacementMatcher {
        @Contract(pure = true)
        @NotNull SymbolicStyle symbolicReset();

        @Contract(pure = true)
        @NotNull Resolver with(@NotNull Ink ink);

        @Contract(pure = true)
        default @NotNull Resolver with(@NotNull Ink @NotNull ... inks) {
            return with(Arrays.asList(inks));
        }

        @NotNull Resolver with(@NotNull Iterable<@NotNull Ink> ink);

        @NotNull InkyMessage inkyMessage();
    }

    public static class Builder implements AbstractBuilder<InkyMessage> {
        private Map<String, Modifier<?>> modifiers;
        private Map<String, Placeholder> placeholders;
        private Map<Character, SymbolicStyle> symbolics;
        private Collection<Replacer> replacers;
        private SymbolicStyle symbolicReset;

        Builder() {
            this.modifiers = new HashMap<>();
            this.placeholders = new HashMap<>();
            this.symbolics = new HashMap<>();
            this.replacers = new HashSet<>();
        }

        Builder(
                @NotNull Map<String, Modifier<?>> modifiers,
                @NotNull Map<String, Placeholder> placeholders,
                @NotNull Map<Character, SymbolicStyle> symbolics,
                @NotNull Collection<Replacer> replacers,
                @Nullable SymbolicStyle symbolicReset
        ) {
            this.modifiers = modifiers;
            this.placeholders = placeholders;
            this.symbolics = symbolics;
            this.replacers = replacers;
            this.symbolicReset = symbolicReset;
        }
        
        public @NotNull InkyMessage.Builder addInk(@NotNull Ink.Stable ink) {
            return switch (ink) {
                case Placeholder ph -> addPlaceholder(ph);
                case Modifier<?> mod -> addModifier(mod);
                case Replacer rp -> addReplacer(rp);
                case SymbolicStyle sym -> addSymbolic(sym);
                default -> throw new IllegalArgumentException("Unknown stable ink type: " + ink.getClass().getSimpleName());
            };
        }

        public @NotNull InkyMessage.Builder addInks(@NotNull Ink.Stable @NotNull ... inks) {
            for (var ink : inks) addInk(ink);
            return this;
        }

        public @NotNull InkyMessage.Builder addInks(@NotNull Iterable<Ink.@NotNull Stable> inks) {
            for (var ink : inks) addInk(ink);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder replacers(@NotNull Replacer @NotNull ... replacers) {
            return replacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder replacers(@NotNull Collection<? extends @NotNull Replacer> replacers) {
            this.replacers = new HashSet<>(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder replacers(@NotNull Iterable<? extends @NotNull Replacer> replacers) {
            this.replacers = new HashSet<>();
            addReplacers(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addReplacer(@NotNull Replacer replacer) {
            this.replacers.add(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addReplacers(@NotNull Replacer @NotNull ... replacers) {
            return addReplacers(Arrays.asList(replacers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addReplacers(@NotNull Collection<? extends @NotNull Replacer> replacers) {
            this.replacers.addAll(replacers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addReplacers(@NotNull Iterable<? extends @NotNull Replacer> replacers) {
            for (var replacer : replacers) addReplacer(replacer);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder placeholders(@NotNull Placeholder @NotNull ... placeholders) {
            return placeholders(Arrays.asList(placeholders));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder placeholders(@NotNull Collection<? extends @NotNull Placeholder> placeholders) {
            this.placeholders = new HashMap<>(placeholders.size());
            addPlaceholders(placeholders);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder placeholders(@NotNull Iterable<? extends @NotNull Placeholder> placeholders) {
            this.placeholders = new HashMap<>();
            addPlaceholders(placeholders);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder placeholders(@NotNull Map<String, Placeholder> placeholders) {
            this.placeholders = new HashMap<>(placeholders);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addPlaceholder(@NotNull Placeholder placeholder) {
            this.placeholders.put(placeholder.name(), placeholder);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addPlaceholders(@NotNull Placeholder @NotNull ... placeholders) {
            return addPlaceholders(Arrays.asList(placeholders));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addPlaceholders(@NotNull Iterable<? extends @NotNull Placeholder> placeholders) {
            for (var placeholder : placeholders) addPlaceholder(placeholder);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addPlaceholders(@NotNull Map<String, Placeholder> placeholders) {
            this.placeholders.putAll(placeholders);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder modifiers(@NotNull Modifier<?> @NotNull ... modifiers) {
            return modifiers(Arrays.asList(modifiers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder modifiers(@NotNull Collection<? extends @NotNull Modifier<?>> modifiers) {
            this.modifiers = new HashMap<>(modifiers.size());
            addModifiers(modifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder modifiers(@NotNull Iterable<? extends @NotNull Modifier<?>> modifiers) {
            this.modifiers = new HashMap<>();
            addModifiers(modifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder modifiers(@NotNull Map<String, Modifier<?>> modifiers) {
            this.modifiers = new HashMap<>(modifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addModifier(@NotNull Modifier<?> modifier) {
            this.modifiers.put(modifier.name(), modifier);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addModifiers(@NotNull Modifier<?> @NotNull ... modifiers) {
            return addModifiers(Arrays.asList(modifiers));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addModifiers(@NotNull Iterable<? extends @NotNull Modifier<?>> modifiers) {
            for (var modifier : modifiers) addModifier(modifier);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addModifiers(@NotNull Map<String, Modifier<?>> modifiers) {
            this.modifiers.putAll(modifiers);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder symbolicReset(char symbol) {
            this.symbolicReset = StandardSymbolicStyles.simpleReset(symbol);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder symbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return symbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder symbolics(@NotNull Collection<? extends @NotNull SymbolicStyle> symbolics) {
            this.symbolics = new HashMap<>(symbolics.size());
            addSymbolics(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder symbolics(@NotNull Map<Character, SymbolicStyle> symbolics) {
            this.symbolics = new HashMap<>(symbolics);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addSymbolic(@NotNull SymbolicStyle symbolic) {
            this.symbolics.put(symbolic.symbol(), symbolic);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addSymbolics(@NotNull SymbolicStyle @NotNull ... symbolics) {
            return addSymbolics(Arrays.asList(symbolics));
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addSymbolics(@NotNull Iterable<? extends @NotNull SymbolicStyle> symbolics) {
            for (var symbolic : symbolics) addSymbolic(symbolic);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull InkyMessage.Builder addSymbolics(@NotNull Map<Character, SymbolicStyle> symbolics) {
            this.symbolics.putAll(symbolics);
            return this;
        }

        @Override
        @Contract("-> new")
        public @NotNull InkyMessage build() {
            return new InkyMessage(
                    new HashMap<>(modifiers),
                    new HashMap<>(placeholders),
                    new HashMap<>(symbolics),
                    new HashSet<>(replacers),
                    Objects.requireNonNull(symbolicReset, "Resolver requires symbolic reset to be provided")
            );
        }
    }
}
