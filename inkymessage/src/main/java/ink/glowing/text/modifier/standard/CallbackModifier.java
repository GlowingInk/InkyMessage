package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

record CallbackModifier(@NotNull @Subst("label") String label, UnaryOperator<Component> mod) implements Modifier {
    CallbackModifier(String label, Supplier<ClickEvent> mod) {
        this(label, text -> text.clickEvent(mod.get()));
    }

    @Override
    public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
        return mod;
    }
}
