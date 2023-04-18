package ink.glowing.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static ink.glowing.text.InkyMessage.inkyMessage;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static org.testng.Assert.assertEquals;

public class InkyMessageTest {
    @DataProvider
    public Object[][] deserializeData() {
        return new Object[][] {
                {
                        "&aGreen &cand red!\\",
                        text().append(text("Green ").color(GREEN)).append(text("and red!\\").color(RED)).build()
                },
                {
                        "&c&lRed and\\\\ bold",
                        text("Red and\\ bold").color(RED).decorate(BOLD)
                },
                {
                        "&lBold &cthen not",
                        text().append(text("Bold ").decorate(BOLD)).append(text("then not").color(RED)).build()
                },
                {
                        "&a&lFirst bold green &rthen&6 gold",
                        text().append(text("First bold green ").color(GREEN).decorate(BOLD)).append(text("then")).append(text(" gold").color(GOLD)).build()
                },
                {
                        "&[Fully clickable\\]](click:run /helloworld)",
                        text("Fully clickable]").clickEvent(ClickEvent.runCommand("/helloworld"))
                },
                {
                        "&aGreen, &[clickable&c red](click:url http://glowing.ink/), red again",
                        text()
                                .append(text("Green, ").color(GREEN))
                                .append(text().clickEvent(ClickEvent.openUrl("http://glowing.ink/"))
                                        .append(text("clickable").color(GREEN))
                                        .append(text(" red").color(RED)))
                                .append(text(", red again").color(RED)).build()
                },
                {
                        "\\&aRegular \\&[text](color:gold), and some&b \\aqua",
                        text("&aRegular &[text](color:gold), and some")
                                .append(text(" \\aqua").color(AQUA))
                },
                {
                        "&x&1&2&3&4&5&6Hex colors are cool",
                        inkyMessage().deserialize("&#123456Hex colors are cool")
                }
        };
    }

    @Test(dataProvider = "deserializeData")
    public void deserializeTest(String text, Component expected) {
        try {
            assertEquals(
                    inkyMessage().deserialize(text),
                    expected
            );
        } catch (Throwable throwable) {
            System.out.println("Inky: " + miniMessage().serialize(inkyMessage().deserialize(text)));
            System.out.println("Mini: " + miniMessage().serialize(expected));
            throw throwable;
        }
    }

    @DataProvider
    public Object[][] escapeData() {
        return new Object[][] {
                {
                    "&a&[Fully clickable](click:run /helloworld)",
                    "\\&a\\&[Fully clickable\\]\\(click:run /helloworld\\)"
                }
        };
    }

    @Test(dataProvider = "escapeData")
    public void escapeTest(String unescaped, String escaped) {
        assertEquals(
                InkyMessage.escape(unescaped),
                escaped
        );
        assertEquals(
                InkyMessage.unescape(escaped),
                unescaped
        );
    }

    @DataProvider
    public Object[][] performanceData() {
        return new Object[][] {
                {
                    "<red>This text is red! <hover:show_text:Cool hover text><click:run_command:test_command><red>Pressing this will <gold>run a command.</click></hover><bold><gold> It's bold yellow",
                    "&cThis text is red! &[Pressing this will &6run a command.](click:run test_command)(hover:text Cool hover text)(color:green)&l It's bold yellow"
                }
        };
    }

    @Test(
            dataProvider = "performanceData",
            description = "The \"test\" exists purely for getting the idea of deserializer performance vs MiniMessage",
            enabled = false
    )
    public void performanceTest(String mini, String inky) {
        var inkyMessage = inkyMessage();
        for (int i = 0; i < 100000; i++) {
            inkyMessage.deserialize(inky);
        }
        var miniMessage = miniMessage();
        for (int i = 0; i < 100000; i++) {
            miniMessage.deserialize(mini);
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            inkyMessage.deserialize(inky);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);

        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            miniMessage.deserialize(mini);
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}