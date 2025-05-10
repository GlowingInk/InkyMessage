package ink.glowing.text.modifier;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

final class ArgumentValueImpl {
    private ArgumentValueImpl() { }
    
    enum EmptyArgument implements Modifier.ArgumentValue {
        INSTANCE;
        
        @Override
        public @NotNull Component asComponent() {
            return Component.empty();
        }

        @Override
        public @NotNull String asString() {
            return "";
        }
    }
    
    record StringArgument(@NotNull String asString) implements Modifier.ArgumentValue {
        @Override
        public @NotNull Component asComponent() {
            return text(asString);
        }
    }

    record ComponentArgument(@NotNull Component asComponent) implements Modifier.ArgumentValue {
        @Override
        public @NotNull String asString() {
            StringBuilder builder = new StringBuilder();
            ComponentFlattener.basic().flatten(asComponent, builder::append);
            return builder.toString();
        }
    }
}
