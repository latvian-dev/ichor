package dev.latvian.apps.ichor.lang.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.error.TokenStreamError;
import dev.latvian.apps.ichor.exit.EndOfFileExit;
import dev.latvian.apps.ichor.token.IdentifierToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.token.TokenPos;
import dev.latvian.apps.ichor.token.TokenSource;
import dev.latvian.apps.ichor.token.TokenStream;
import dev.latvian.apps.ichor.util.IchorUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

public class TokenStreamJS implements TokenStream {
	private final TokenSource tokenSource;
	private final char[] input;
	private final String[] lines;
	private int pos;
	private int row;
	private int col;
	private int prevPos;
	private int prevRow;
	private int prevCol;
	private int tokenCount;
	private PositionedToken rootToken;
	private PositionedToken currentToken;
	private final Map<String, Number> numberTokenCache;
	private final Map<String, Token> nameTokenCache;
	private final Stack<SymbolTokenJS> depth;
	private SymbolTokenJS currentDepth;
	private final long timeout;
	private long timeoutTime;

	public TokenStreamJS(Context cx, TokenSource source, String string) {
		tokenSource = source;
		input = string.toCharArray();
		lines = string.split("\n");
		pos = 0;
		row = 1;
		col = 1;
		numberTokenCache = new HashMap<>();
		nameTokenCache = new HashMap<>();
		depth = new Stack<>();
		currentDepth = null;
		timeout = cx.getTokenStreamTimeout();
		timeoutTime = 0L;
	}

	private Token makeName(String s) {
		return nameTokenCache.computeIfAbsent(s, IdentifierToken::new);
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
		col++;

		if (c == '\n') {
			row++;
			col = 1;
		} else if (c == '\t') {
			input[pos - 1] = ' ';
			return ' ';
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
		return new TokenStreamError(new TokenPos(tokenSource, row, col), msg, lines[row - 1].replace('\n', ' '));
	}

	private static boolean isDigit(char t) {
		return t >= '0' && t <= '9';
	}

	private static boolean isHex(char t) {
		return isDigit(t) || (t >= 'a' && t <= 'f') || (t >= 'A' && t <= 'F');
	}

	private Object readToken() {
		if (currentDepth == SymbolTokenJS.TEMPLATE_LITERAL && peek(1) != '`' && !(peek(1) == '$' && peek(2) == '{')) {
			var sb = new StringBuilder();

			while (true) {
				char c = peek(1);

				if (c == '\\') {
					read();
					char n = read();

					if (n == '\\') {
						sb.append('\\');
						sb.append('\\');
					} else {
						sb.append(n == '\n' ? '\n' : readEscape(n));
					}
				} else if (c == '`' || c == '$' && peek(2) == '{') {
					break;
				} else {
					read();
					sb.append(c);
				}
			}

			return sb.toString();
		}

		var t = readSkippingWhitespace();
		var s = SymbolTokenJS.read(this, t);

		if (s == SymbolTokenJS.COMMENT_LINE) {
			while (true) {
				t = read();

				if (t == '\n') {
					return readToken();
				}
			}
		} else if (s == SymbolTokenJS.COMMENT_BLOCK_START) {
			while (true) {
				t = read();

				if (t == '*' && peek(1) == '/') {
					read();
					return readToken();
				}
			}
		} else if (s == SymbolTokenJS.DIV && (currentToken == null || currentToken.token instanceof Token tk && tk.isLiteralPre())) {
			var sbP = new StringBuilder();
			var sbF = new StringBuilder();

			while (true) {
				char c = read();

				if (c == '\\' && peek(1) == '/') {
					read();
					sbP.append('\\');
					sbP.append('/');
				} else if (c == '/') {
					char p = peek(1);

					while (p >= 'a' && p <= 'z' || p >= 'A' && p <= 'Z') {
						sbF.append(read());
						p = peek(1);
					}

					break;
				} else if (c == '\n') {
					throw error("Newline isn't allowed in RegEx!");
				} else {
					sbP.append(c);
				}
			}

			int flags = 0;

			for (int i = 0; i < sbF.length(); i++) {
				switch (sbF.charAt(i)) {
					case 'd' -> flags |= Pattern.UNIX_LINES;
					case 'i' -> flags |= Pattern.CASE_INSENSITIVE;
					case 'x' -> flags |= Pattern.COMMENTS;
					case 'm' -> flags |= Pattern.MULTILINE;
					case 's' -> flags |= Pattern.DOTALL;
					case 'u' -> flags |= Pattern.UNICODE_CASE;
					case 'U' -> flags |= Pattern.UNICODE_CHARACTER_CLASS;
					case 'g' -> {
					}
					default -> throw error("Invalid RegEx flag: '" + sbF.charAt(i) + "'");
				}
			}

			return Pattern.compile(sbP.toString(), flags);
		} else if (s == SymbolTokenJS.DOT && isDigit(peek(1))) {
			return readNumber(t); // fix this
		} else if (s == SymbolTokenJS.SSTRING || s == SymbolTokenJS.DSTRING) {
			var sb = new StringBuilder();

			while (true) {
				char c = read();

				if (c == '\\') {
					sb.append(readEscape(read()));
				} else if (c == t) {
					break;
				} else if (c == '\n') {
					throw error("Newline isn't allowed in regular strings!");
				} else {
					sb.append(c);
				}
			}

			return sb.toString();
		} else if (s != null) {
			if (s == SymbolTokenJS.TEMPLATE_LITERAL_VAR || s == SymbolTokenJS.LC || s == SymbolTokenJS.LP || s == SymbolTokenJS.LS) {
				depth.push(s);
				currentDepth = s;
			} else if (s == SymbolTokenJS.RC || s == SymbolTokenJS.RP || s == SymbolTokenJS.RS) {
				if (currentDepth == null || depth.isEmpty()) {
					throw error("Unexpected closing bracket " + s);
				}

				test(SymbolTokenJS.TEMPLATE_LITERAL_VAR, s, SymbolTokenJS.RC);
				test(SymbolTokenJS.LC, s, SymbolTokenJS.RC);
				test(SymbolTokenJS.LP, s, SymbolTokenJS.RP);
				test(SymbolTokenJS.LS, s, SymbolTokenJS.RS);

				depth.pop();
				currentDepth = depth.isEmpty() ? null : depth.peek();
			} else if (s == SymbolTokenJS.TEMPLATE_LITERAL) {
				if (currentDepth == SymbolTokenJS.TEMPLATE_LITERAL) {
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

	private void test(SymbolTokenJS ifToken, SymbolTokenJS symbol, SymbolTokenJS expected) {
		if (currentDepth == ifToken && symbol != expected) {
			throw error("Expected '" + ifToken + "' to be closed with '" + expected + "', got '" + symbol + "' instead");
		}
	}

	private Object readLiteral(char t) {
		if (isDigit(t)) {
			return readNumber(t);
		} else if (Character.isJavaIdentifierStart(t)) {
			int p = pos - 1;
			int len = 1;

			while (true) {
				char c = peek(1);

				if (c != 0 && Character.isJavaIdentifierPart(c)) {
					len++;
					read();
				} else {
					break;
				}
			}

			var nameStr = new String(input, p, len);
			var k = KeywordTokenJS.CACHE.get(nameStr);
			return k != null ? k : makeName(nameStr);
		} else {
			throw error("Unexpected token: " + t);
		}
	}

	private Number readNumber(char init) {
		// TODO: Support for exp, hex, oct, bin, bigint
		// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Lexical_grammar#numeric_literals

		boolean hasDec = false;

		int p = pos - 1;
		int len = 1;

		while (true) {
			char c = peek(1);

			if (c == '.' && isDigit(peek(2))) {
				if (hasDec) {
					throw error("Number can't contain decimal point twice!");
				} else {
					hasDec = true;
					len++;
					read();
				}
			} else if (isDigit(c) || len >= 1 && (len == 2 && c == '-' && Character.toLowerCase(input[p + 1]) == 'e' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F')) {
				len++;
				read();
			} else {
				break;
			}
		}

		var numStr = new String(input, p, len);

		var num = numberTokenCache.get(numStr);

		if (num == null) {
			try {
				num = IchorUtils.parseNumber(numStr);
			} catch (NumberFormatException ex) {
				throw error("Invalid number: " + numStr);
			}

			numberTokenCache.put(numStr, num);
		}

		return num;
	}

	private int readHex() {
		char c = read();

		if (c >= '0' && c <= '9') {
			return c - '0';
		} else if (c >= 'a' && c <= 'f') {
			return c - 'a' + 10;
		} else if (c >= 'A' && c <= 'F') {
			return c - 'A' + 10;
		} else {
			throw error("Invalid hex code: " + c);
		}
	}

	private int readX() {
		int a = readHex();
		int b = readHex();
		return (a << 4) + b;
	}

	private int readU() {
		int a = readHex();
		int b = readHex();
		int c = readHex();
		int d = readHex();
		return (a << 12) + (b << 8) + (c << 4) + d;
	}

	private char readEscape(char c) {
		if (c < ' ') {
			throw error("Can't escape whitespace!");
		}

		return switch (c) {
			case '\\', '"', '\'', '`', '/' -> c;
			case 'n' -> '\n';
			case 't' -> '\t';
			case 'b' -> '\b';
			case 'f' -> '\f';
			case 'r' -> '\r';
			case 'e' -> '\u001B';
			case 'x', 'X' -> (char) readX();
			case 'u', 'U' -> (char) readU();
			case '0', '1', '2', '3', '4', '5', '6', '7' -> {
				int num = 0;

				do {
					num *= 8;
					num += c - '0';
					c = read();
				}
				while (c >= '0' && c <= '7');

				if (num > 255) {
					throw error("Invalid escape character: '" + c + "'");
				}

				yield (char) num;
			}
			// case 'v' -> '\v';
			default -> throw error("Invalid escape character: '" + c + "'");
		};
	}

	private void insertToken(PositionedToken newCurrent) {
		if (rootToken == null) {
			tokenCount = 1;
			rootToken = newCurrent;
			currentToken = newCurrent;
			newCurrent.prev = PositionedToken.NONE;
			newCurrent.next = PositionedToken.NONE;
		} else {
			tokenCount++;
			var prev = currentToken;
			prev.next = newCurrent;

			currentToken = newCurrent;
			currentToken.prev = prev;
			currentToken.next = PositionedToken.NONE;
		}
	}

	public PositionedToken getRootToken() {
		if (rootToken != null) {
			return rootToken;
		}

		timeoutTime = timeout <= 0L ? 0L : (System.currentTimeMillis() + timeout);

		while (true) {
			prevPos = pos;
			prevRow = row;
			prevCol = col;

			try {
				var newCurrent = new PositionedToken(readToken(), new TokenPos(tokenSource, prevRow, prevCol));

				if (prevRow != row && currentToken != null && currentToken.token instanceof Token tk) {
					var tbn = tk.getTokenBeforeNewline();

					if (tbn != null && tbn != newCurrent.token) {
						insertToken(new PositionedToken(tbn, new TokenPos(tokenSource, prevRow, prevCol)));
					}
				}

				insertToken(newCurrent);
			} catch (EndOfFileExit exit) {
				if (currentDepth != null) {
					throw error("Expected '" + currentDepth + "' to be closed!");
				}

				return rootToken;
			}
		}
	}

	public int getTokenCount() {
		getRootToken();
		return tokenCount;
	}

	@Override
	public String toString() {
		return rootToken.toRecursiveString();
	}
}
