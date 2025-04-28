package example;

import ink.glowing.text.InkyMessage;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
import static ink.glowing.text.replace.Replacer.replacer;
import static ink.glowing.text.replace.StandardReplacers.URL_PATTERN;
import static ink.glowing.text.symbolic.standard.StandardSymbolicStyles.notchianFormat;
import static ink.glowing.text.symbolic.standard.StandardSymbolicStyles.resettingColor;
import static ink.glowing.text.utils.function.Caching.caching;
import static io.papermc.paper.chat.ChatRenderer.viewerUnaware;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.openUrl;
import static net.kyori.adventure.text.format.TextColor.color;

public class InkyMessageChatPaper extends JavaPlugin implements Listener { // TODO Permission-based format
    private static final String FORMAT =
            "&{sender:display_name}(click:suggest /tell &{sender:name} ) &8>&h &{message}(hover:text Sent at:&{newline}&{time})";

    private static final String WWW = "www.";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final InkyMessage CHAT_FORMATTER = inkyMessage().with(
            placeholder("time", () -> text(LocalTime.now().format(TIME_FORMAT))),
            resettingColor('h', color(200, 200, 200))
    );
    private static final InkyMessage PLAYER_INPUT = inkyMessage(
            'r',
            inkProvider(notchianFormat()),
            replacer(
                    URL_PATTERN,
                    (match) -> {
                        String fullUrl = match.group();

                        String domain = match.group(1);
                        if (domain.startsWith(WWW) && domain.indexOf('.', WWW.length()) == domain.lastIndexOf('.')) {
                            domain = domain.substring(WWW.length());
                        }
                        String add = match.group(2);

                        if (add != null) {
                            if (add.length() > 13) {
                                add = add.substring(0, 5) + "..." + add.substring(add.length() - 5);
                            }
                        } else {
                            add = "";
                        }

                        return text(domain + add).style((builder) -> builder
                                .decorate(TextDecoration.ITALIC)
                                .clickEvent(openUrl(fullUrl))
                                .hoverEvent(text(fullUrl))
                        );
                    }
            )
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
                placeholder(
                        "message",
                        caching(() -> PLAYER_INPUT.deserialize(
                                event.signedMessage()
                                        .message()
                                        .replace("\\", "\\\\")
                                        .replace("&[", "\\&[")
                        )))
        );
        event.renderer(viewerUnaware((player, displayName, message) -> result));
    }
}
