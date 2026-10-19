package ink.glowing.text.extra.paper;

import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.utils.Labeled.LabelPattern;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static ink.glowing.text.placeholder.Placeholder.placeholder;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

// TODO Statistic, ip, location, more?
/**
 * Placeholders based on Paper API.
 */
public final class PaperPlaceholders {
    private PaperPlaceholders() { }

    /**
     * Creates a placeholder of player info, labeled {@code player}, e.g. {@code &{player:name}}.
     * Parameter is the requested info: {@code display_name}, {@code name}, {@code uuid}, {@code locale} or {@code locale_tag}.
     * @param player player to get info of
     * @return a new placeholder
     */
    public static @NotNull Placeholder playerPlaceholder(@NotNull Player player) {
        return playerPlaceholder("player", player);
    }

    /**
     * Creates a placeholder of player info.
     * Parameter is the requested info: {@code display_name}, {@code name}, {@code uuid}, {@code locale} or {@code locale_tag}.
     * @param prefix label of placeholder
     * @param player player to get info of
     * @return a new placeholder
     */
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
