package ink.glowing.text.modifier.standard;

import ink.glowing.text.Context;
import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

final class CallbackModifier implements Modifier {
    private final @Subst("label") String label;
    private final UnaryOperator<Component> mod;

    CallbackModifier(String label, Supplier<ClickEvent> eventSupplier) {
        this.label = label;
        this.mod = text -> text.clickEvent(eventSupplier.get());
    }
    
    @Override
    public @NotNull @LabelPattern String label() {
        return label;
    }

    @Override
    public @NotNull UnaryOperator<Component> prepareModification(@NotNull Arguments arguments, @NotNull Context context) {
        return mod;
    }
}
