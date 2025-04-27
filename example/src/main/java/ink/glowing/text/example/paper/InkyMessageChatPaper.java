package ink.glowing.text.example.paper;

import ink.glowing.text.InkyMessage;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static ink.glowing.text.Ink.inkProvider;
import static ink.glowing.text.InkyMessage.inkyMessage;
import static ink.glowing.text.extra.paper.PaperPlaceholders.playerPlaceholder;
import static ink.glowing.text.placeholder.Placeholder.placeholder;
import static ink.glowing.text.replace.StandardReplacers.fancyUrlReplacer;
import static ink.glowing.text.symbolic.standard.StandardSymbolicStyles.notchianFormat;
import static ink.glowing.text.utils.function.Caching.caching;
import static io.papermc.paper.chat.ChatRenderer.viewerUnaware;
import static net.kyori.adventure.text.Component.text;

public class InkyMessageChatPaper extends JavaPlugin implements Listener { // TODO
    private static final String FORMAT =
            "&{sender:display_name}(click:suggest /tell &{sender:name} ) > &{message}(hover:text &{time})";
    private static final InkyMessage CHAT_FORMATTER = inkyMessage();
    private static final InkyMessage PLAYER_INPUT = inkyMessage(
            'r',
            inkProvider(notchianFormat()),
            fancyUrlReplacer(),
            placeholder("time", () -> text(LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)))
    );

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent event) {
        Component result = CHAT_FORMATTER.deserialize(
                FORMAT,
                playerPlaceholder("sender", event.getPlayer()),
                placeholder("message", caching(() -> PLAYER_INPUT.deserialize(event.signedMessage().message())))
        );
        event.renderer(viewerUnaware((player, displayName, message) -> result));
    }
}
