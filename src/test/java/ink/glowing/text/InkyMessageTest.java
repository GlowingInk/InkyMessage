package ink.glowing.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import static ink.glowing.text.InkyMessage.inkyMessage;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static org.testng.Assert.assertEquals;

public class InkyMessageTest {
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean debug = false;

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
                        text("Fully clickable]").clickEvent(runCommand("/helloworld"))
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
                },
                {
                        "&aGoto https://github.com/GlowingInk.",
                        text()
                                .append(text("Goto ").color(GREEN))
                                .append(text("https://github.com/GlowingInk").clickEvent(ClickEvent.openUrl("https://github.com/GlowingInk")).color(GREEN))
                                .append(text(".").color(GREEN)).build()
                },
                {
                        "&[aaa&[bbb&[ccc](decor:bold)bbb](decor:italic)&faaa](color:red)",
                        text("aaa").color(RED)
                                .append(text("bbb").decorate(ITALIC).append(text("ccc").decorate(BOLD)).append(text("bbb")))
                                .append(text("aaa").color(WHITE))
                },
                {
                        "&cSome &[hover parsing](hover:text &atest!).",
                        text()
                                .append(text("Some ").color(RED))
                                .append(text("hover parsing").color(RED).hoverEvent(showText(text("test!").color(GREEN))))
                                .append(text(".").color(RED)).build()
                }
        };
    }

    @Test(dataProvider = "deserializeData")
    public void deserializeTest(String text, Component expected) {
        if (debug) debugDeserializer(text, expected);
        try {
            assertEquals(
                    inkyMessage().deserialize(text),
                    expected
            );
        } catch (Throwable throwable) {
            if (!debug) debugDeserializer(text, expected);
            throw throwable;
        }
    }

    private void debugDeserializer(String text, Component comp) {
        System.out.println("Inky: " + miniMessage().serialize(inkyMessage().deserialize(text)));
        System.out.println("Mini: " + miniMessage().serialize(comp));
    }

    @DataProvider
    public Object[][] serializeData() {
        return new Object[][] {
                {
                        text("Hi"),
                        "Hi"
                },
                {
                        text("Green").color(GREEN),
                        "&aGreen"
                },
                {
                        text().append(text("Italic ").decorate(ITALIC)).append(text("then bold").decorate(BOLD)).build(),
                        "&oItalic &r&lthen bold"
                },
                {
                        text()
                                .append(text("Bold green").color(GREEN).decorate(BOLD))
                                .append(text(" and "))
                                .append(text("blue").color(BLUE)).build(),
                        "&a&lBold green&r and &9blue"
                },
                {
                        text("With hover").hoverEvent(showText(text("Hover!"))),
                        "&[With hover](hover:text Hover!)"
                },
                {
                        text("Hover and click").hoverEvent(showText(text("Hover!"))).clickEvent(runCommand("cmd")),
                        "&[Hover and click](hover:text Hover!)(click:run cmd)"
                },
                {
                        text()
                                .append(text("With "))
                                .append(text("inner ")
                                        .append(text("deep").clickEvent(runCommand("cmd")))
                                .append(text(" component")).hoverEvent(showText(text("Wow")))).build(),
                        "With &[inner &[deep](click:run cmd) component](hover:text Wow)"
                },
                {
                        text()
                                .append(text("First green").color(GREEN).decorate(BOLD))
                                .append(text(" then second green").color(GREEN).decorate(BOLD)).build(),
                        "&a&lFirst green&a&l then second green"
                },
                {
                        text("First green").append(text(" then second green")).color(GREEN).decorate(BOLD),
                        "&a&lFirst green&a&l then second green"
                }
        };
    }

    @Test(dataProvider = "serializeData")
    public void serializeTest(Component text, String expected) {
        if (debug) debugSerializer(text);
        try {
            assertEquals(
                    inkyMessage().serialize(text),
                    expected
            );
        } catch (Throwable throwable) {
            if (!debug) debugSerializer(text);
            throw throwable;
        }
    }

    private void debugSerializer(Component comp) {
        System.out.println("Inky: " + inkyMessage().serialize(comp));
        System.out.println("Mini: " + miniMessage().serialize(comp));
    }

    @DataProvider
    public Object[][] serializeAndBackData() {
        return new Object[][] {
                {text("Test")},
                {text("Green").color(GREEN)},
                {text()
                        .append(text("Bold green").color(GREEN).decorate(BOLD))
                        .append(text(" and "))
                        .append(text("blue").color(BLUE)).build()}
        };
    }

    @Test(dataProvider = "serializeAndBackData")
    public void serializeAndBackTest(Component text) {
        String ser = inkyMessage().serialize(text);
        Component deser = inkyMessage().deserialize(ser);
        assertEquals(
                inkyMessage().serialize(deser),
                ser
        );
    }

    private static final String SYMBOLS = "abcde&[](){}\\:";

    @Test(description = "Basically hoping that we'll get no exceptions while creating a hot stinky mess")
    public void randomTest() {
        RandomGenerator rng = ThreadLocalRandom.current();
        for (int i = 0; i < 2048; i++) {
            StringBuilder builder = new StringBuilder(256);
            for (int j = 0; j < 256; j++) {
                builder.append(SYMBOLS.charAt(rng.nextInt(SYMBOLS.length())));
            }
            inkyMessage().deserialize(builder.toString());
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
    public Object[][] isEscapedData() {
        return new Object[][] {
                {"\\&[test]", 1, true},
                {"\\\\&[test]", 1, true},
                {"\\\\&[test]", 2, false},
                {"\\\\\\&[test]", 3, true},
                {"\\\\\\&[test]", 4, false}
        };
    }

    @Test(dataProvider = "isEscapedData")
    public void isEscapedTest(String input, int index, boolean expected) {
        assertEquals(
                InkyMessage.isEscapedAt(input, index),
                expected
        );
    }

    @DataProvider
    public Object[][] performanceData() {
        return new Object[][] {
                {
                        "<red>This text is red! <hover:show_text:Cool hover text><click:run_command:test_command><red>Pressing this will <gold>run a command.</click></hover><bold><gold> It's bold yellow",
                        "&cThis text is red! &[Pressing this will &6run a command.](click:run test_command)(hover:text Cool hover text)&l It's bold yellow"
                },
                {
                        "<gradient:white:black:yellow:red>qwertyuiopasdfghjkl;'zxcvbnm,.</gradient>",
                        "&[qwertyuiopasdfghjkl;'zxcvbnm,.](gradient:white-black-yellow-red)"
                },
                {
                        "<rainbow>qwertyuiopasdfghjkl;'zxcvbnm,.</rainbow>",
                        "&[qwertyuiopasdfghjkl;'zxcvbnm,.](gradient:rainbow)"
                }
        };
    }

    @Test(
            dataProvider = "performanceData",
            description = "The \"test\" exists purely for getting a rough idea of deserializer performance vs MiniMessage",
            enabled = false
    )
    public void performanceTest(String mini, String inky) {
        int warmup = 100000;
        int test = 10000;

        var inkyMessage = inkyMessage();
        for (int i = 0; i < warmup; i++) {
            inkyMessage.deserialize(inky);
        }
        var miniMessage = miniMessage();
        for (int i = 0; i < warmup; i++) {
            miniMessage.deserialize(mini);
        }

        long start, end;

        start = System.currentTimeMillis();
        for (int i = 0; i < test; i++) {
            inkyMessage.deserialize(inky);
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);

        start = System.currentTimeMillis();
        for (int i = 0; i < test; i++) {
            miniMessage.deserialize(mini);
        }
        end = System.currentTimeMillis();
        System.out.println(end - start);

        System.out.println("Inky: " + miniMessage.serialize(inkyMessage.deserialize(inky)));
        System.out.println("<reset>");
        System.out.println("Mini: " + miniMessage.serialize(miniMessage.deserialize(mini)));
    }
}