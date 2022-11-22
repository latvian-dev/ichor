package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.error.TokenStreamError;
import dev.latvian.apps.ichor.exit.EndOfFileExit;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.NameToken;
import dev.latvian.apps.ichor.token.NumberToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.StringToken;
import dev.latvian.apps.ichor.token.SymbolToken;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.token.TokenPos;
import dev.latvian.apps.ichor.token.TokenSource;
import dev.latvian.apps.ichor.token.TokenStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class TokenStreamJS implements TokenStream {
	private final TokenSource tokenSource;
	private final char[] input;
	private int pos;
	private int row;
	private int col;
	private List<PositionedToken> tokens;
	private final Map<String, NumberToken> numberTokenCache;
	private final Map<String, StringToken> stringTokenCache;
	private final Map<String, NameToken> nameTokenCache;
	private final Stack<SymbolToken> depth;
	private SymbolToken currentDepth;
	private long timeoutTime;
	private long timeout;

	public TokenStreamJS(TokenSource source, String string) {
		tokenSource = source;
		input = string.toCharArray();
		pos = 0;
		row = 1;
		col = 1;
		numberTokenCache = new HashMap<>();
		stringTokenCache = new HashMap<>();
		nameTokenCache = new HashMap<>();
		depth = new Stack<>();
		currentDepth = null;
		timeoutTime = 0L;
		timeout = 5000L;
	}

	public TokenStreamJS timeout(long t) {
		timeout = t;
		return this;
	}

	private StringToken makeString(String s) {
		return stringTokenCache.computeIfAbsent(s, StringToken::of);
	}

	private NameToken makeName(String s) {
		return nameTokenCache.computeIfAbsent(s, NameToken::new);
	}

	private char peek(int i) {
		return pos + i - 1 >= input.length ? 0 : input[pos + i - 1];
	}

	private char read() {
		if (timeoutTime > 0L && System.currentTimeMillis() > timeoutTime) {
			throw error("Timeout");
		}

		if (pos >= input.length) {
			throw new EndOfFileExit();
		}

		char c = input[pos++];

		if (c == '\n') {
			row++;
			col = 1;
		}

		return c;
	}

	@Override
	public boolean readIf(char c) {
		if (peek(1) == c) {
			read();
			return true;
		}

		return false;
	}

	private char readSkippingWhitespace() {
		char t = read();

		while (t <= ' ') {
			if (t == 0) {
				return 0;
			} else {
				t = read();
			}
		}

		return t;
	}

	private TokenStreamError error(String msg) {
		return new TokenStreamError(new TokenPos(tokenSource, row, pos), msg);
	}

	private static boolean isDigit(char t) {
		return t >= '0' && t <= '9';
	}

	private Token readToken() {
		if (currentDepth == SymbolToken.TEMPLATE_LITERAL && peek(1) != '`' && !(peek(1) == '$' && peek(2) == '{')) {
			var sb = new StringBuilder();

			while (true) {
				char c = peek(1);

				if (c == '\\') {
					read();
					char n = read();
					sb.append(n == '\n' ? '\n' : readEscape(n));
				} else if (c == '`' || c == '$' && peek(2) == '{') {
					break;
				} else {
					read();
					sb.append(c);
				}
			}

			return makeString(sb.toString());
		}

		var t = readSkippingWhitespace();

		if (t == '/') {
			if (peek(1) == '/') {
				while (true) {
					t = read();

					if (t == '\n') {
						t = readSkippingWhitespace();
						break;
					}
				}
			} else if (peek(1) == '*') {
				read();

				while (true) {
					t = read();

					if (t == '*' && peek(1) == '/') {
						read();
						t = readSkippingWhitespace();
						break;
					}
				}
			}
		}

		if (t == '.' && isDigit(peek(1))) {
			return readNumber();
		} else if (t == '\'' || t == '"') {
			var sb = new StringBuilder();

			while (true) {
				char c = read();

				if (c == '\\') {
					sb.append(readEscape(read()));
				} else if (c == t) {
					break;
				} else if (c == '\n') {
					throw error("Newline isn't allowed!");
				} else {
					sb.append(c);
				}
			}

			return makeString(sb.toString());
		}

		var s = SymbolToken.read(this, t);

		if (s != null) {
			if (s == SymbolToken.TEMPLATE_LITERAL_VAR || s == SymbolToken.LC || s == SymbolToken.LP || s == SymbolToken.LS) {
				depth.push(s);
				currentDepth = s;
			} else if (s == SymbolToken.RC || s == SymbolToken.RP || s == SymbolToken.RS) {
				if (currentDepth == null || depth.isEmpty()) {
					throw error("Unexpected closing bracket " + s);
				}

				test(SymbolToken.TEMPLATE_LITERAL_VAR, s, SymbolToken.RC);
				test(SymbolToken.LC, s, SymbolToken.RC);
				test(SymbolToken.LP, s, SymbolToken.RP);
				test(SymbolToken.LS, s, SymbolToken.RS);

				depth.pop();
				currentDepth = depth.isEmpty() ? null : depth.peek();
			} else if (s == SymbolToken.TEMPLATE_LITERAL) {
				if (currentDepth == SymbolToken.TEMPLATE_LITERAL) {
					depth.pop();
					currentDepth = depth.isEmpty() ? null : depth.peek();
				} else {
					depth.push(s);
					currentDepth = s;
				}
			}

			return s;
		}

		return readLiteral(t);
	}

	private void test(SymbolToken ifToken, SymbolToken symbol, SymbolToken expected) {
		if (currentDepth == ifToken && symbol != expected) {
			throw error("Expected '" + ifToken + "' to be closed with '" + expected + "', got '" + symbol + "' instead");
		}
	}

	private Token readLiteral(char t) {
		if (isDigit(t)) {
			return readNumber();
		} else if (Character.isJavaIdentifierStart(t)) {
			int p = pos - 1;
			int len = 1;

			while (true) {
				char c = peek(1);

				if (Character.isJavaIdentifierPart(c)) {
					len++;
					read();
				} else {
					break;
				}
			}

			var nameStr = new String(input, p, len);
			var k = KeywordToken.MAP.get(nameStr);
			return k != null ? k.toLiteralOrSelf() : makeName(nameStr);
		} else {
			throw error("Unexpected token: " + t);
		}
	}

	private Token readNumber() {
		// TODO: Support for exp, hex, oct, bin, bigint
		// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Lexical_grammar#numeric_literals

		boolean hasDec = false;

		int p = pos - 1;
		int len = 1;

		while (true) {
			char c = peek(1);

			if (c == '.') {
				if (isDigit(peek(2))) {
					if (hasDec) {
						throw error("Number can't contain decimal point twice!");
					} else {
						hasDec = true;
						len++;
						read();
					}
				} else {
					break;
				}
			} else if (isDigit(c)) {
				len++;
				read();
			} else {
				break;
			}
		}

		var numStr = new String(input, p, len);

		var num = numberTokenCache.get(numStr);

		if (num != null) {
			return num;
		}

		try {
			num = new NumberToken(Double.parseDouble(numStr));
			numberTokenCache.put(numStr, num);
			return num;
		} catch (Exception ex) {
			throw error("Invalid number: " + numStr);
		}
	}

	private char readEscape(char c) {
		if (c < ' ') {
			throw error("Can't escape whitespace!");
		}

		return switch (c) {
			case '\\', '"', '\'', '`' -> c;
			case 'n' -> '\n';
			case 't' -> '\t';
			case 'b' -> '\b';
			case 'f' -> '\f';
			case 'r' -> '\r';
			// case 'v' -> '\v';
			default -> throw error("Invalid escape character: '" + c + "'");
		};
	}

	public List<PositionedToken> getTokens() {
		if (tokens == null) {
			tokens = new ArrayList<>();
		} else {
			return tokens;
		}

		timeoutTime = timeout <= 0L ? 0L : (System.currentTimeMillis() + timeout);

		while (true) {
			int _row = row;
			int _col = col;

			try {
				tokens.add(new PositionedToken(readToken(), new TokenPos(tokenSource, _row, _col)));
			} catch (EndOfFileExit exit) {
				if (currentDepth != null) {
					throw error("Expected '" + currentDepth + "' to be closed!");
				}

				return tokens;
			}
		}
	}

	@Override
	public String toString() {
		return getTokens().toString();
	}
}
