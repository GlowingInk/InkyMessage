package ink.glowing.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;

import static ink.glowing.text.InkyMessage.inkyMessage;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public final class Helper {
    public static Component mini(String str) {
        return miniMessage().deserialize(str);
    }

    public static Component inky(String str) {
        return inkyMessage().deserialize(str);
    }

    public static String mini(Component text) {
        return miniMessage().serialize(text);
    }

    public static String inky(Component text) {
        return inkyMessage().serialize(text);
    }

    public static Component t(String text) {
        return text(text);
    }

    public static TextComponent.Builder tb() {
        return text();
    }

    public static TextComponent.Builder tb(ComponentLike... texts) {
        return text().append(texts);
    }
}
