package ink.glowing.text;

import ink.glowing.text.symbolic.SymbolicStyle;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

import static ink.glowing.text.InkyMessage.isSpecial;

public class TokenizedParser {
    int index = 0;
    final List<Token> tokens;

    public TokenizedParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static Component parse(List<Token> tokens, Context ctx) {
        return new TokenizedParser(tokens).parseWhile(ctx, (i) -> true);
    }

    public Component parseWhile(Context ctx, IntPredicate condition) {
        return Component.empty(); // TODO
    }

    public static List<Token> tokenize(String inputStr, Context ctx) {
        char[] input = inputStr.toCharArray();
        List<Token> tokens = new ArrayList<>();
        int lastAppend = 0;
        for (int index = 0; index < input.length; index++) {
            char ch = input[index];
            Token found = switch (ch) {
                case '&' -> {
                    int nextIndex = index + 1;
                    if (nextIndex >= input.length) {
                        yield null;
                    }
                    char nextCh = input[nextIndex];
                    yield switch (nextCh) {
                        case '{' -> TokenType.PLACEHOLDER_START.token();
                        case '[' -> TokenType.SEGMENT_START.token();
                        case '(' -> TokenType.MODIFIER_START.token();
                        case 'x', '#' -> // TODO
                                TokenType.HEX_COLOR.token("#123456");
                        default -> {
                            SymbolicStyle symbolic = ctx.findSymbolicStyle(nextCh);
                            yield symbolic != null
                                    ? TokenType.SYMBOLIC_STYLE.token(Character.toString(nextCh))
                                    : null;
                        }
                    };
                }
                case '\\' -> {
                    int nextIndex = index + 1;
                    if (nextIndex >= input.length) {
                        yield null;
                    }
                    char nextCh = input[nextIndex];
                    if (isSpecial(nextCh)) {
                        index++;
                        yield TokenType.TEXT.token(Character.toString(nextCh));
                    } else {
                        yield null;
                    }
                }
                case '{' -> TokenType.PLACEHOLDER_OPEN.token();
                case '[' -> TokenType.SEGMENT_OPEN.token();
                case '(' -> TokenType.MODIFIER_OPEN.token();
                case '}' -> TokenType.PLACEHOLDER_CLOSE.token();
                case ']' -> TokenType.SEGMENT_CLOSE.token();
                case ')' -> TokenType.MODIFIER_CLOSE.token();
                default -> null;
            };
            if (found != null) {
                if (index > lastAppend) {
                    String localText = new String(input, lastAppend, index - lastAppend);
                    tokens.add(TokenType.TEXT.token(localText));
                }
                tokens.add(found);
                lastAppend = (index += found.text.length() - 1) + 1;
            }
        }
        return tokens;
    }

    record Token(TokenType type, String text) { }

    enum TokenType {
        TEXT, SYMBOLIC_STYLE, HEX_COLOR,
        PLACEHOLDER_START("&{"), PLACEHOLDER_OPEN("{"), PLACEHOLDER_CLOSE("}"), 
        SEGMENT_START("&["), SEGMENT_OPEN("["), SEGMENT_CLOSE("]"), 
        MODIFIER_START("&("), MODIFIER_OPEN("("), MODIFIER_CLOSE(")");
        
        private final Token token;

        TokenType(String defaultText) {
            this.token = new Token(this, defaultText);
        }
        
        TokenType() {
            this.token = new Token(this, "");
        }

        public Token token() {
            return token;
        }

        public Token token(String text) {
            return new Token(this, text);
        }
    }
}
