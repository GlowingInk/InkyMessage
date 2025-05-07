package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.replace.ReplacementMatcher;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.symbolic.SymbolicStyle;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import static ink.glowing.text.Stringifier.stringify;
import static ink.glowing.text.modifier.standard.StandardModifiers.standardModifiers;
import static ink.glowing.text.placeholder.StandardPlaceholders.newlinePlaceholder;
import static ink.glowing.text.placeholder.StandardPlaceholders.requiredPlaceholdersMap;
import static ink.glowing.text.replace.ReplacementMatcher.replacementMatcher;
import static ink.glowing.text.replace.StandardReplacers.urlReplacer;
import static ink.glowing.text.symbolic.standard.StandardSymbolicStyles.notchianFormat;
import static ink.glowing.text.symbolic.standard.StandardSymbolicStyles.standardResetSymbol;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableMap;

final class InkyMessageImpl implements InkyMessage {
    static final InkyMessage STANDARD = InkyMessage.builder()
            .addPlaceholder(newlinePlaceholder())
            .addModifiers(standardModifiers())
            .addSymbolics(notchianFormat())
            .symbolicReset(standardResetSymbol())
            .addReplacer(urlReplacer())
            .build();
    
    private final Map<String, Modifier> modifiers;
    private final Map<String, Placeholder> placeholders;
    private final Map<Character, SymbolicStyle> symbolics;
    private final Collection<Replacer> replacers;
    private final SymbolicStyle symbolicReset;

    private final Context baseContext;
    private final ReplacementMatcher replacementMatcher;

    InkyMessageImpl(
            @NotNull Map<String, Modifier> modifiers,
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

        this.replacementMatcher = replacementMatcher(replacers);

        Map<String, Placeholder> adjustedPlaceholders = new HashMap<>(placeholders);
        adjustedPlaceholders.putAll(requiredPlaceholdersMap());
        Map<Character, SymbolicStyle> adjustedSymbolics = new HashMap<>(symbolics);
        adjustedSymbolics.put(symbolicReset.symbol(), symbolicReset);
        this.baseContext = new Context(
                this,
                modifiers::get,
                adjustedPlaceholders::get,
                adjustedSymbolics::get,
                replacementMatcher,
                symbolicReset
        );
    }

    @Override
    public @Unmodifiable @NotNull Map<String, Modifier> modifiers() {
        return modifiers;
    }

    @Override
    public @Nullable Modifier findModifier(@NotNull String label) {
        return modifiers.get(label);
    }

    @Override
    public @Unmodifiable @NotNull Map<String, Placeholder> placeholders() {
        return placeholders;
    }

    @Override
    public @Nullable Placeholder findPlaceholder(@NotNull String label) {
        return placeholders.get(label);
    }

    @Override
    public @Unmodifiable @NotNull Map<Character, SymbolicStyle> symbolics() {
        return symbolics;
    }

    @Override
    public @Nullable SymbolicStyle findSymbolicStyle(char symbol) {
        return symbolics.get(symbol);
    }

    @Override
    public @Unmodifiable @NotNull Collection<Replacer> replacers() {
        return replacers;
    }

    @Override
    public @NotNull TreeSet<Replacer.FoundSpot> matchReplacements(@NotNull String input) {
        return replacementMatcher.matchReplacements(input);
    }

    @Override
    public @NotNull SymbolicStyle symbolicReset() {
        return symbolicReset;
    }

    @Override
    public @NotNull Context baseContext() {
        return baseContext;
    }

    @Override
    public @NotNull Component deserialize(@NotNull String inputText) {
        return deserialize(inputText, this.baseContext);
    }

    @Override
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Ink ink) {
        return deserialize(inputText, this.baseContext.with(ink));
    }

    @Override
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Ink @NotNull ... inks) {
        return deserialize(inputText, this.baseContext.with(inks));
    }

    @Override
    public @NotNull Component deserialize(@NotNull String inputText,
                                          @NotNull Iterable<? extends @NotNull Ink> inks) {
        return deserialize(inputText, this.baseContext.with(inks));
    }

    /**
     * Convert string into adventure text component.
     * @param inputText input string
     * @param context context to use
     * @return converted text component
     */
    private static @NotNull Component deserialize(@NotNull String inputText,
                                                  @NotNull Context context) {
        return Parser.parse(inputText, context);
    }

    @Override
    public @NotNull String serialize(@NotNull Component text) {
        return stringify(text, this);
    }
}
