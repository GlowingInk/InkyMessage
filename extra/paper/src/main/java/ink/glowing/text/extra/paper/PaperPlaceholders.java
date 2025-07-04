package ink.glowing.text.extra.paper;

import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.utils.Labeled.LabelPattern;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static ink.glowing.text.placeholder.Placeholder.placeholder;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

// TODO Statistic, ip, location, more?
public final class PaperPlaceholders {
    private PaperPlaceholders() {}

    public static @NotNull Placeholder playerPlaceholder(@NotNull Player player) {
        return playerPlaceholder("player", player);
    }

    public static @NotNull Placeholder playerPlaceholder(@NotNull @LabelPattern String prefix, @NotNull Player player) {
        return placeholder(prefix, (value) -> switch (value) {
            case "display_name" -> player.displayName();
            case "name" -> player.name();
            case "uuid" -> text(player.getUniqueId().toString());
            case "locale" -> text(player.locale().toString());
            case "locale_tag" -> text(player.locale().toLanguageTag());
            default -> empty();
        });
    }
}
