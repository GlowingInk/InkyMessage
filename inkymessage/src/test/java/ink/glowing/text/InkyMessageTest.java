package ink.glowing.text;

import ink.glowing.text.modifier.standard.StandardModifiers;
import ink.glowing.text.placeholder.Placeholder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import static ink.glowing.text.Helper.*;
import static ink.glowing.text.InkyMessage.inkyMessage;
import static ink.glowing.text.placeholder.Placeholder.placeholder;
import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.openUrl;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static org.testng.Assert.assertEquals;

public class InkyMessageTest {
    private static final boolean DEBUG = false;

    @DataProvider
    public Object[][] deserializeData() {
        return new Object[][] {
                {
                        "&aGreen &cand red!\\",
                        tb(t("Green ").color(GREEN), t("and red!\\").color(RED))
                }, {
                        "&[&c&lRed and\\\\ bold]",
                        tb(tb(t("Red and\\ bold").color(RED).decorate(BOLD)))
                }, {
                        "&lBold &cthen just red",
                        tb(t("Bold ").decorate(BOLD), t("then just red").color(RED))
                }, {
                        "&a&lFirst bold green &rthen&c red",
                        tb(t("First bold green ").color(GREEN).decorate(BOLD), t("then"), t(" red").color(RED))
                }, {
                        "&[Fully clickable\\]](click:run /helloworld)",
                        tb(tb(t("Fully clickable]")).clickEvent(runCommand("/helloworld")))
                }, {
                        "&aGreen, &[clickable&c red](click:url https://github.com/GlowingInk), red again",
                        tb(
                                t("Green, ").color(GREEN),
                                tb(
                                        t("clickable").color(GREEN),
                                        t(" red").color(RED)
                                ).clickEvent(openUrl("https://github.com/GlowingInk")),
                                t(", red again").color(RED)
                        )
                }, {
                        "\\&aRegular \\&[text](color:gold), and some&a \\green",
                        tb(t("&aRegular &[text](color:gold), and some"), t(" \\green").color(GREEN))
                }, {
                        "&aGoto https://github.com/GlowingInk and https://repo.glowing.ink.", // TODO This should be parsed into much simpler component
                        tb(
                                t("Goto ").color(GREEN),
                                t("https://github.com/GlowingInk").clickEvent(openUrl("https://github.com/GlowingInk")).color(GREEN),
                                t(" and ").color(GREEN),
                                t("https://repo.glowing.ink").clickEvent(openUrl("https://repo.glowing.ink")).color(GREEN),
                                t(".").color(GREEN)
                        )
                }, {
                        "&[aaa&[bbb&[ccc](decor:bold)bbb](decor:italic)&aaaa](color:red)",
                        tb(
                                tb(
                                        t("aaa"),
                                        tb(
                                                t("bbb"),
                                                tb(
                                                        t("ccc")
                                                ).decorate(BOLD),
                                                t("bbb")
                                        ).decorate(ITALIC),
                                        t("aaa").color(GREEN)
                                ).color(RED)
                        )
                }, {
                        "&cSome &[hover parsing](hover:text &atest!).",
                        tb(
                                t("Some ").color(RED),
                                tb(t("hover parsing").color(RED)).hoverEvent(showText(tb(t("test!").color(GREEN)))),
                                t(".").color(RED)
                        )
                }, {
                        "&[Test](fake:modifier lol), &[another one].",
                        tb(
                                tb(t("Test")),
                                t("(fake:modifier lol), "),
                                tb(t("another one")),
                                t(".")
                        )
                }, {
                        "&[Test](hover:text Hover)&[ and another](decor:cursive)",
                        tb(
                                tb(t("Test")).hoverEvent(tb(t("Hover")).build()),
                                tb(t(" and another")).decorate(ITALIC))
                }, {
                        "Test &{lang:my.cool.test}(arg &aHello world)(arg Yay...)(fallback Falling back)&{lang:test}",
                        tb(
                                t("Test "),
                                translatable("my.cool.test")
                                        .arguments(
                                                tb(t("Hello world").color(GREEN)),
                                                tb(t("Yay...")))
                                        .fallback("Falling back"),
                                translatable("test")
                        )
                }, {
                        "&aPress &{keybind:sneak} to sneak!", // TODO This should be parsed into much simpler component
                        tb(
                                t("Press ").color(GREEN),
                                keybind("sneak").color(GREEN),
                                t(" to sneak!").color(GREEN)
                        )
                }, {
                        "&{lang:test}(arg:4 four args)",
                        tb(translatable("test").arguments(empty(), empty(), empty(), tb(t("four args"))))
                }, {
                        "&<&aPlain text\\> &[This one too](color:green)> &agreen",
                        tb(
                                t("&aPlain text> &[This one too](color:green)"),
                                t(" "),
                                t("green").color(GREEN)
                        )
                },
        };
    }

    @Test(dataProvider = "deserializeData")
    public void deserializeTest(String text, ComponentLike expectedLike) {
        Component expected = expectedLike.asComponent();
        if (DEBUG) debugDeserializer(text, expected);
        try {
            assertEquals(
                    inky(text),
                    expected.compact()
            );
        } catch (Throwable throwable) {
            if (!DEBUG) debugDeserializer(text, expected);
            throw throwable;
        }
    }

    @DataProvider
    public Object[][] deserializerReverseData() {
        return new Object[][] {
                {
                        "&(color:red)[This one is red]",
                        "&[This one is red](color:red)"
                }, {
                        "&(color:red)(decor:italic)[Well, this is awkward](decor:bold)", // TODO Should be either left or right - not both
                        "&[Well, this is awkward](color:red)(decor:bold)(decor:italic)"
                }, {
                        "&(color:red)Skipped",
                        "&(color:red)Skipped"
                }
        };
    }

    @Test(dataProvider = "deserializerReverseData")
    public void deserializerReverseTest(String reverse, String normal) {
        assertEquals(
                inky(reverse),
                inky(normal)
        );
    }

    @Test
    public void deserializeHexTest() {
        assertEquals(
                inky("&x&1&2&3&4&5&6Hex colors are cool"),
                inky("&#123456Hex colors are cool")
        );
    }

    @DataProvider
    public Object[][] deserializeWithInksData() {
        return new Object[][] {
                {
                        "&{test1} &{test2}",
                        Set.of(
                                placeholder("test1", t("Hello")),
                                placeholder("test2", t("world"))
                        ),
                        tb(t("Hello"), t(" "), t("world"))
                }, {
                        "&[Test inline](click:run /&{cmd} &{arg})",
                        Set.of(
                                Placeholder.literalPlaceholder("cmd", "command"),
                                placeholder("arg", text("argument"))
                        ),
                        tb(tb(t("Test inline")).clickEvent(ClickEvent.runCommand("/command argument")))
                }, {
                        "&{lang:more_values}(args [First] [Second](color:red) [&aThi rd])",
                        Set.of(
                                StandardModifiers.langArgsModifier()
                        ),
                        tb(translatable("more_values").arguments(
                                tb(t("First")),
                                tb(t("Second")).color(RED),
                                tb(t("Thi rd").color(GREEN))
                        ))
                }
        };
    }

    @Test(dataProvider = "deserializeWithInksData")
    public void deserializeWithInksTest(String text, Collection<Ink> inks, ComponentLike expectedLike) {
        Component expected = expectedLike.asComponent();
        assertEquals(
                inkyMessage().deserialize(text, inks),
                expected.compact()
        );
    }

    private void debugDeserializer(String text, Component comp) {
        System.out.println("Inky -----");
        System.out.println(mini(inky(text)));
        System.out.println("<reset>");
        System.out.println("Mini -----");
        System.out.println(mini(comp));
    }

    @DataProvider
    public Object[][] serializeData() {
        return new Object[][] {
                {
                        t("Hi").color(TextColor.color(0x123456)),
                        "&#123456Hi"
                }, {
                        t("Green").color(GREEN),
                        "&aGreen"
                }, {
                        tb(t("Italic ").decorate(ITALIC)).append(t("then bold").decorate(BOLD)),
                        "&oItalic &r&lthen bold"
                }, {
                        tb(
                                t("Bold green").color(GREEN).decorate(BOLD),
                                t(" and "),
                                t("red").color(RED)),
                        "&a&lBold green&r and &cred"
                }, {
                        t("With hover").hoverEvent(showText(t("Hover!"))),
                        "&[With hover](hover:text Hover!)"
                }, {
                        t("Hover and click").hoverEvent(showText(t("&aHover!"))).clickEvent(runCommand("cmd")),
                        "&[Hover and click](hover:text \\&aHover!)(click:run cmd)"
                }, {
                        tb(
                                t("With "),
                                t("inner ").append(
                                        t("deep").clickEvent(runCommand("cmd")),
                                        t(" component")
                                ).hoverEvent(t("Wow"))
                        ),
                        "With &[inner &[deep](click:run cmd) component](hover:text Wow)"
                }, {
                        t("That's so ").append(
                                translatable("cool", "Fallbacked", t("Arg1"), t("Arg2\\")).append(
                                        t(" Test")
                                ).hoverEvent(t("hover!").color(GREEN))
                        ),
                        "That's so &[&{lang:cool}(arg Arg1)(arg Arg2\\\\)(fallback Fallbacked) Test](hover:text &ahover!)"
                }
        };
    }

    @Test(dataProvider = "serializeData")
    public void serializeTest(ComponentLike textLike, String expected) {
        Component text= textLike.asComponent();
        if (DEBUG) debugSerializer(text);
        try {
            assertEquals(
                    inky(text),
                    expected
            );
        } catch (Throwable throwable) {
            if (!DEBUG) debugSerializer(text);
            throw throwable;
        }
    }

    @DataProvider
    public Object[][] idealSerializerData() { // TODO
        return new Object[][] {
                {
                        tb(
                                t("First green").color(GREEN).decorate(BOLD),
                                t(" then second green").color(GREEN).decorate(BOLD)
                        ),
                        "&a&lFirst green then second green"
                }, {
                        t("First green").append(t(" then second green")).color(GREEN).decorate(BOLD),
                        "&a&lFirst green then second green"
                }, {
                        tb( // Probably won't happen
                                t("Outside ").color(GREEN),
                                t("inside 1 ").color(GREEN).clickEvent(runCommand("/home")),
                                t(" inside 2").color(RED).clickEvent(runCommand("/home"))
                        ),
                        "&aOutside &[inside 1&c inside 2](click:run /home)"
                }
        };
    }

    @Test(dataProvider = "idealSerializerData", enabled = false)
    public void idealSerializerTest(ComponentLike textLike, String expected) {
        Component text = textLike.asComponent();
        serializeTest(text, expected);
    }

    private void debugSerializer(Component comp) {
        System.out.println("Inky: " + inky(comp));
        System.out.println("Mini: " + mini(comp));
    }

    @DataProvider
    public Object[][] serializeAndBackData() {
        return new Object[][] {
                {t("Test")},
                {t("Green").color(GREEN)},
                {tb(
                        t("Bold green").color(GREEN).decorate(BOLD),
                        t(" and "),
                        t("red").color(RED)
                )}
        };
    }

    @Test(dataProvider = "serializeAndBackData")
    public void serializeAndBackTest(ComponentLike textLike) {
        Component text = textLike.asComponent();
        String ser = inky(text);
        Component deser = inky(ser);
        assertEquals(
                inky(deser),
                ser
        );
    }

    private static final String SYMBOLS = "&[](){}\\:#x";

    @Test(description = "Basically hoping that we'll get no exceptions while parsing a hot stinky mess")
    public void randomTest() {
        RandomGenerator rng = ThreadLocalRandom.current();
        for (int i = 0; i < 1024; i++) {
            StringBuilder builder = new StringBuilder(512);
            for (int j = 0; j < 512; j++) {
                builder.append(SYMBOLS.charAt(rng.nextInt(SYMBOLS.length())));
            }
            try {
                inky(builder.toString());
            } catch (Throwable throwable) {
                System.out.println(builder);
                throw throwable;
            }
        }
    }

    @DataProvider
    public Object[][] escapeData() {
        return new Object[][] {
                {
                    "&a&[Not \\clickable](click:run /helloworld)",
                    "\\&a\\&\\[Not \\\\clickable\\]\\(click:run /helloworld\\)"
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
    public Object[][] isEscapedAtData() {
        return new Object[][] {
                {"\\&[test]", 1, true},
                {"\\\\&[test]", 1, true},
                {"\\\\&[test]", 2, false},
                {"\\\\\\&[test]", 3, true},
                {"\\\\\\&[test]", 4, false}
        };
    }

    @Test(dataProvider = "isEscapedAtData")
    public void isEscapedAtTest(String input, int index, boolean expected) {
        assertEquals(
                InkyMessage.isEscapedAt(input, index),
                expected
        );
    }

    @DataProvider
    public Object[][] performanceData() {
        return new Object[][] {
                {
                        "&cThis text is red! &[Pressing this will &6run a command.](click:run test_command)(hover:text Cool hover text)&l It's bold gold",
                        "<red>This text is red! <hover:show_text:Cool hover text><click:run_command:test_command><red>Pressing this will <gold>run a command.</click></hover><bold><gold> It's bold gold"
                }, {
                        "&[qwertyuiopasdfghjkl;&{lang:test}(arg Test)'zxcvbnm,.](color:white-black-yellow-red)",
                        "<gradient:white:black:yellow:red>qwertyuiopasdfghjkl;<lang:test:'Test'>'zxcvbnm,.</gradient>"
                }, {
                        "&[qwertyuiopasdfghjkl;'zxcvbnm,.](color:spectrum)",
                        "<rainbow>qwertyuiopasdfghjkl;'zxcvbnm,.</rainbow>"
                }
        };
    }

    @Test(
            dataProvider = "performanceData",
            description = "The \"test\" exists purely for getting a *rough* idea of deserializer performance vs MiniMessage",
            enabled = false
    )
    public void performanceTest(String inky, String mini) {
        int warmup = 100000;
        int test = 10000;

        var inkyMessage = inkyMessage();
        var miniMessage = miniMessage();
        for (int i = 0; i < warmup; i++) {
            inkyMessage.deserialize(inky);
            miniMessage.deserialize(mini);
        }

        long start, end;

        start = System.nanoTime();
        for (int i = 0; i < test; i++) {
            inkyMessage.deserialize(inky);
        }
        end = System.nanoTime();
        System.out.println((end - start) / test);

        start = System.nanoTime();
        for (int i = 0; i < test; i++) {
            miniMessage.deserialize(mini);
        }
        end = System.nanoTime();
        System.out.println((end - start) / test);

        System.out.println("Inky: " + miniMessage.serialize(inkyMessage.deserialize(inky)));
        System.out.println("<reset>");
        System.out.println("Mini: " + miniMessage.serialize(miniMessage.deserialize(mini)));
    }
}