package ink.glowing.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static org.testng.Assert.assertEquals;

public class InkyMessageTest {
    @DataProvider
    public Object[][] deserializeData() {
        return new Object[][] {
                {
                        "&aGreen!\\",
                        text("Green!\\").color(GREEN)
                },
                {
                        "&c&lRed and bold",
                        empty() .append(text("Red and bold").color(RED).decorate(BOLD))
                },
                {
                        "&lBold &cthen not",
                        empty()
                                .append(text("Bold ").decorate(BOLD))
                                .append(text("then not").color(RED))
                },
                {
                        "&aFirst green &6then gold",
                        empty()
                                .append(text("First green ").color(GREEN))
                                .append(text("then gold").color(GOLD))
                },
                {
                        "&[Fully clickable](click:run /helloworld)",
                        text("Fully clickable").clickEvent(ClickEvent.runCommand("/helloworld"))
                },
                {
                        "&aGreen, &[some red](color:red), green again",
                        empty()
                                .append(text("Green, ").color(GREEN))
                                .append(text("some red").color(RED))
                                .append(text(", green again").color(GREEN))
                },
                {
                        "&aGreen, &[clickable&c red](click:url https://glowing.ink/), red again",
                        empty()
                                .append(text("Green, ").color(GREEN))
                                .append(text("clickable").append(text(" red").color(RED)).clickEvent(ClickEvent.openUrl("https://glowing.ink/")))
                                .append(text(", red again").color(RED))
                },
                {
                        "\\&aRegular \\&[text](color:gold), and some&b \\aqua",
                        text("&aRegular &[text](color:gold), and some")
                                .append(text(" \\aqua").color(AQUA))
                }
        };
    }

    @Test(dataProvider = "deserializeData")
    public void deserializeTest(String text, Component expected) {
        try {
            assertEquals(
                    InkyMessage.INSTANCE.deserialize(text),
                    expected
            );
        } catch (Throwable throwable) {
            System.out.println(MiniMessage.miniMessage().serialize(InkyMessage.INSTANCE.deserialize(text)));
            System.out.println(MiniMessage.miniMessage().serialize(expected));
            throw throwable;
        }
    }

    @DataProvider
    public Object[][] escapeSingularSlashData() {
        return new Object[][] {
                {"\\", "\\\\"},
                {"\\\\", "\\\\"},
                {"\\a", "\\\\a"},
                {"\\&Abc", "\\&Abc"}
        };
    }

    @Test(dataProvider = "escapeSingularSlashData")
    public void escapeSingularSlashTest(String text, String expected) {
        assertEquals(InkyMessage.escapeSingularSlash(text), expected);
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
                InkyMessage.escapeAll(unescaped),
                escaped
        );
        assertEquals(
                InkyMessage.unescapeAll(escaped),
                unescaped
        );
    }
}