package ink.glowing.text.modifier.standard;

import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

final class CallbackModifier implements Modifier.Plain {
    private final @Subst("name") String name;
    private final Supplier<ClickEvent> eventSupplier;

    CallbackModifier(String name, Supplier<ClickEvent> eventSupplier) {
        this.name = name;
        this.eventSupplier = eventSupplier;
    }

    @Override
    public @NotNull Component modify(@NotNull Component text, @NotNull String param, @NotNull String value) {
        return text.clickEvent(eventSupplier.get());
    }
    
    @Override
    public @NotNull @NamePattern String name() {
        return name;
    }
}
