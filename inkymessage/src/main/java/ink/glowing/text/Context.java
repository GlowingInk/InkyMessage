package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierFinder;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.placeholder.PlaceholderFinder;
import ink.glowing.text.replace.ReplacementMatcher;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.symbolic.SymbolicStyle;
import ink.glowing.text.symbolic.SymbolicStyleFinder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.ComponentDecoder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static ink.glowing.text.replace.ReplacementMatcher.composeReplacementMatchers;
import static ink.glowing.text.replace.ReplacementMatcher.replacementMatcher;

final class Context implements ModifierFinder, PlaceholderFinder, SymbolicStyleFinder, ReplacementMatcher,
        ComponentDecoder<String, Component> {
    private final InkyMessage inkyMessage;

    private final ModifierFinder modifiers;
    private final PlaceholderFinder placeholders;
    private final SymbolicStyleFinder symbolics;
    private final ReplacementMatcher replacers;
    private final SymbolicStyle symbolicReset;

    private Style lastStyle;

    Context(
            @NotNull InkyMessage inkyMessage,
            @NotNull ModifierFinder modifiers,
            @NotNull PlaceholderFinder placeholders,
            @NotNull SymbolicStyleFinder symbolics,
            @NotNull ReplacementMatcher replacers,
            @NotNull SymbolicStyle symbolicReset
    ) {
        this.inkyMessage = inkyMessage;
        this.modifiers = modifiers;
        this.placeholders = placeholders;
        this.symbolics = symbolics;
        this.replacers = replacers;
        this.symbolicReset = symbolicReset;

        this.lastStyle = Style.empty();
    }

    @Override
    public @NotNull Component deserialize(@NotNull String textStr) {
        return Parser.parse(textStr, stylelessCopy());
    }

    @ApiStatus.Internal
    @Contract(pure = true, value = "-> new")
    @NotNull Context stylelessCopy() {
        return new Context(inkyMessage, modifiers, placeholders, symbolics, replacers, symbolicReset);
    }

    @ApiStatus.Internal
    @Contract(pure = true)
    @NotNull Style lastStyle() {
        return lastStyle;
    }

    @ApiStatus.Internal
    void lastStyle(@NotNull Style lastStyle) {
        this.lastStyle = lastStyle;
    }

    @Override
    public @Nullable Modifier findModifier(@NotNull String name) {
        return modifiers.findModifier(name);
    }

    @Override
    public @Nullable Placeholder findPlaceholder(@NotNull String name) {
        return placeholders.findPlaceholder(name);
    }

    @Override
    public @Nullable SymbolicStyle findSymbolicStyle(char symbol) {
        return symbolics.findSymbolicStyle(symbol);
    }

    @Override
    public @NotNull TreeSet<Replacer.FoundSpot> matchReplacements(@NotNull String input) {
        return replacers.matchReplacements(input);
    }

    @Contract(pure = true)
    public @NotNull InkyMessage inkyMessage() {
        return inkyMessage;
    }

    @Contract(pure = true)
    public @NotNull SymbolicStyle symbolicReset() {
        return symbolicReset;
    }

    public @NotNull Context with(@NotNull Ink ink) {
        var modifiers = this.modifiers;
        var placeholders = this.placeholders;
        var symbolics = this.symbolics;
        var replacers = this.replacers;

        switch (ink) {
            case Modifier mod    -> modifiers = modifiers.thenModifierFinder(mod);
            case Placeholder ph     -> placeholders = placeholders.thenPlaceholderFinder(ph);
            case SymbolicStyle sym  -> symbolics = symbolics.thenSymbloicStyleFinder(sym);
            case Replacer rep       -> replacers = composeReplacementMatchers(rep, replacers);
            default -> throw new IllegalArgumentException("Unknown ink type: " + ink.getClass().getSimpleName());
        }

        return new Context(this.inkyMessage, modifiers, placeholders, symbolics, replacers, this.symbolicReset);
    }

    public @NotNull Context with(@NotNull Ink @NotNull ... inks) {
        return with(Arrays.asList(inks));
    }

    public @NotNull Context with(@NotNull Iterable<? extends @NotNull Ink> inks) {
        var modifiers = this.modifiers;
        var placeholders = this.placeholders;
        var symbolics = this.symbolics;
        var replacers = this.replacers;

        var modifiersMap = new HashMap<String, Modifier>();
        var placeholdersMap = new HashMap<String, Placeholder>();
        var symbolicsMap = new HashMap<Character, SymbolicStyle>();
        var replacersSet = new HashSet<Replacer>();

        with(inks, modifiersMap, placeholdersMap, symbolicsMap, replacersSet);

        if (!modifiersMap.isEmpty()) {
            modifiers = modifiers.thenModifierFinder(modifiersMap::get);
        }
        if (!placeholdersMap.isEmpty()) {
            placeholders = placeholders.thenPlaceholderFinder(placeholdersMap::get);
        }
        if (!symbolicsMap.isEmpty()) {
            symbolics = symbolics.thenSymbloicStyleFinder(symbolicsMap::get);
        }
        if (!replacersSet.isEmpty()) {
            replacers = composeReplacementMatchers(replacers, replacementMatcher(replacersSet));
        }

        return new Context(this.inkyMessage, modifiers, placeholders, symbolics, replacers, this.symbolicReset);
    }

    private static void with(@NotNull Iterable<? extends @NotNull Ink> inks,
                             @NotNull Map<String, Modifier> modifiersMap,
                             @NotNull Map<String, Placeholder> placeholdersMap,
                             @NotNull Map<Character, SymbolicStyle> symbolicsMap,
                             @NotNull Set<Replacer> replacersSet) {
        for (Ink ink : inks) {
            switch (ink) {
                case Modifier mod -> modifiersMap.put(mod.name(), mod);
                case Placeholder ph -> placeholdersMap.put(ph.name(), ph);
                case SymbolicStyle sym -> symbolicsMap.put(sym.symbol(), sym);
                case Replacer rep -> replacersSet.add(rep);
                case Ink.Provider pr -> with(pr.inks(), modifiersMap, placeholdersMap, symbolicsMap, replacersSet);
                default-> throw new IllegalArgumentException("Unknown ink type: " + ink.getClass().getSimpleName());
            }
        }
    }
}
