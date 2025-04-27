package ink.glowing.text.extra.paper;

import ink.glowing.text.placeholder.Placeholder;
import ink.glowing.text.utils.Named.NamePattern;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static ink.glowing.text.placeholder.Placeholder.inlinedPlaceholder;
import static ink.glowing.text.placeholder.Placeholder.placeholder;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

public class PaperPlaceholders {
    public static @NotNull List<Placeholder> playerPlaceholders(@NotNull Player player) {
        return Arrays.asList(
                placeholder("display_name", player.displayName()),
                inlinedPlaceholder("name", player.getName()),
                inlinedPlaceholder("uuid", player.getUniqueId().toString()),
                inlinedPlaceholder("locale", player.locale().toString()),
                inlinedPlaceholder("locale_tag", player.locale().toLanguageTag())
        );
    }

    public static @NotNull Placeholder playerPlaceholder(@NotNull Player player) {
        return playerPlaceholder("player", player);
    }

    public static @NotNull Placeholder playerPlaceholder(@NotNull @NamePattern String prefix, @NotNull Player player) {
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
