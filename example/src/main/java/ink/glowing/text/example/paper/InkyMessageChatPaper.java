package ink.glowing.text.example.paper;

import ink.glowing.text.InkyMessage;
import io.papermc.paper.chat.ChatRenderer;
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
import static net.kyori.adventure.text.Component.text;

public class InkyMessageChatPaper extends JavaPlugin implements Listener {
    private static final String FORMAT =
            "&{sender:display_name}(click:suggest /tell &{sender:name} ) > &{message}"; // TODO
    private static final InkyMessage CHAT_FORMATTER = inkyMessage();
    private static final InkyMessage PLAYER_INPUT = inkyMessage(
            'r',
            inkProvider(notchianFormat()),
            fancyUrlReplacer(),
            placeholder("time", (ignored) -> text(LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)))
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
                placeholder("message", () -> PLAYER_INPUT.deserialize(event.signedMessage().message()))
        );
        event.renderer(ChatRenderer.viewerUnaware((player, displayName, message) -> result));
    }
}
