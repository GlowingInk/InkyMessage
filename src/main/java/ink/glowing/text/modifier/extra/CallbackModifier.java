package ink.glowing.text.modifier.extra;

import ink.glowing.text.modifier.Modifier;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CallbackModifier implements Modifier.Plain {
    private final @Subst("name") String name;
    private final Supplier<ClickEvent> eventSupplier;

    private CallbackModifier(String name, Supplier<ClickEvent> eventSupplier) {
        this.name = name;
        this.eventSupplier = eventSupplier;
    }

    public @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                    @NotNull Runnable action) {
        return new CallbackModifier(name, () -> ClickEvent.callback(audience -> action.run()));
    }

    public @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                    @NotNull Consumer<Audience> action) {
        return new CallbackModifier(name, () -> ClickEvent.callback(action::accept));
    }

    public @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                    @NotNull ClickCallback<Audience> action) {
        return new CallbackModifier(name, () -> ClickEvent.callback(action));
    }

    public @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                    @NotNull ClickCallback<Audience> action,
                                                    @NotNull ClickCallback.Options options) {
        return new CallbackModifier(name, () -> ClickEvent.callback(action, options));
    }

    public @NotNull Modifier.Plain callbackModifier(@NotNull @NamePattern String name,
                                                    @NotNull ClickCallback<Audience> action,
                                                    @NotNull Consumer<ClickCallback.Options. @NotNull Builder> optionsBuilder) {
        return new CallbackModifier(name, () -> ClickEvent.callback(action, optionsBuilder));
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
