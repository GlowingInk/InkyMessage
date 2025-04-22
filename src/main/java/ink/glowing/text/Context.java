package ink.glowing.text;

import ink.glowing.text.modifier.Modifier;
import ink.glowing.text.modifier.ModifierFinder;
import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.placeholder.PlaceholderFinder;
import ink.glowing.text.replace.ReplacementMatcher;
import ink.glowing.text.replace.Replacer;
import ink.glowing.text.symbolic.SymbolicStyle;
import ink.glowing.text.symbolic.SymbolicStyleFinder;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static ink.glowing.text.replace.ReplacementMatcher.composeReplacementMatchers;
import static ink.glowing.text.replace.ReplacementMatcher.replacementMatcher;

@ApiStatus.Internal
final class Context implements ModifierFinder, PlaceholderFinder, SymbolicStyleFinder, ReplacementMatcher {
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

    @Contract(pure = true, value = "-> new")
    public @NotNull Context stylelessCopy() {
        return new Context(inkyMessage, modifiers, placeholders, symbolics, replacers, symbolicReset);
    }

    @Contract(pure = true)
    public @NotNull Style lastStyle() {
        return lastStyle;
    }

    public void lastStyle(@NotNull Style lastStyle) {
        this.lastStyle = lastStyle;
    }

    @Override
    public @Nullable Modifier<?> findModifier(@NotNull String name) {
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
            case PlaceholderFinder ph     -> placeholders = placeholders.thenPlaceholderFinder(ph);
            case ModifierFinder mod       -> modifiers = modifiers.thenModifierFinder(mod);
            case SymbolicStyleFinder sym  -> symbolics = symbolics.thenSymbloicStyleFinder(sym);
            case ReplacementMatcher rep   -> replacers = composeReplacementMatchers(rep, replacers);
            default -> throw new IllegalArgumentException("Unknown ink type: " + ink.getClass().getSimpleName());
        }

        return new Context(this.inkyMessage, modifiers, placeholders, symbolics, replacers, this.symbolicReset);
    }

    public @NotNull Context with(@NotNull Ink @NotNull ... inks) {
        return with(Arrays.asList(inks));
    }

    public @NotNull Context with(@NotNull Iterable<@NotNull Ink> inks) {
        var modifiers = this.modifiers;
        var placeholders = this.placeholders;
        var symbolics = this.symbolics;
        var replacers = this.replacers;

        Map<String, Modifier<?>> modifiersMap = null;
        Map<String, Placeholder> placeholdersMap = null;
        Map<Character, SymbolicStyle> symbolicStylesMap = null;
        Set<Replacer> replacersSet = null;

        for (var ink : inks) {
            switch (ink) {
                case Placeholder ph -> {
                    if (placeholdersMap == null) {
                        placeholdersMap = new HashMap<>();
                        placeholders = placeholders.thenPlaceholderFinder(placeholdersMap::get);
                    }
                    placeholdersMap.put(ph.name(), ph);
                }
                case PlaceholderFinder ph -> placeholders = ph.thenPlaceholderFinder(placeholders);

                case Modifier<?> mod -> {
                    if (modifiersMap == null) {
                        modifiersMap = new HashMap<>();
                        modifiers = modifiers.thenModifierFinder(modifiersMap::get);
                    }
                    modifiersMap.put(mod.name(), mod);
                }
                case ModifierFinder mod -> modifiers = mod.thenModifierFinder(modifiers);

                case SymbolicStyle sym -> {
                    if (symbolicStylesMap == null) {
                        symbolicStylesMap = new HashMap<>();
                        symbolics = symbolics.thenSymbloicStyleFinder(symbolicStylesMap::get);
                    }
                    symbolicStylesMap.put(sym.symbol(), sym);
                }
                case SymbolicStyleFinder sym -> symbolics = sym.thenSymbloicStyleFinder(symbolics);

                case Replacer rep -> {
                    if (replacersSet == null) {
                        replacersSet = new HashSet<>();
                    }
                    replacersSet.add(rep);
                }
                case ReplacementMatcher rep -> replacers = composeReplacementMatchers(rep, replacers);

                default -> throw new IllegalArgumentException("Unknown ink type: " + ink.getClass().getSimpleName());
            }
        }
        if (replacersSet != null) {
            replacers = composeReplacementMatchers(replacers, replacementMatcher(replacersSet));
        }
        return new Context(this.inkyMessage, modifiers, placeholders, symbolics, replacers, this.symbolicReset);
    }
}
