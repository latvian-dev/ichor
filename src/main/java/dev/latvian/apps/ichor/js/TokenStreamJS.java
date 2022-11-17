package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.TokenSource;
import dev.latvian.apps.ichor.error.TokenStreamError;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.NameToken;
import dev.latvian.apps.ichor.token.NumberToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.StringToken;
import dev.latvian.apps.ichor.token.SymbolToken;
import dev.latvian.apps.ichor.token.TemplateLiteralToken;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.token.TokenPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenStreamJS {
	private final TokenSource tokenSource;
	private final char[] input;
	private int line;
	private int pos;
	private List<PositionedToken> tokens;
	private final Map<String, NumberToken> numberTokenCache;
	private final Map<String, StringToken> stringTokenCache;
	private final Map<String, TemplateLiteralToken> templateLiteralTokenMap;

	public TokenStreamJS(TokenSource source, String string) {
		tokenSource = source;
		input = string.toCharArray();
		line = 0;
		pos = 0;
		numberTokenCache = new HashMap<>();
		stringTokenCache = new HashMap<>();
		templateLiteralTokenMap = new HashMap<>();
	}

	private char peek(int i) {
		return pos + i - 1 >= input.length ? 0 : input[pos + i - 1];
	}

	private char read() {
		if (pos >= input.length) {
			return 0;
		}

		char c = input[pos++];

		if (c == '\n') {
			line++;
		}

		return c;
	}

	private boolean readIf(char c) {
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

	private Token error(String msg) {
		throw new TokenStreamError(new TokenPos(tokenSource, line, pos), msg);
	}

	private static boolean isDigit(char t) {
		return t >= '0' && t <= '9';
	}

	private static boolean isName(char t) {
		return t >= 'a' && t <= 'z' || t >= 'A' && t <= 'Z' || t == '_' || t == '$' || t >= '0' && t <= '9';
	}

	private Token readToken() {
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

		return switch (t) {
			case 0 -> SymbolToken.EOF;
			case '.' -> {
				if (isDigit(peek(1))) {
					yield readNumber();
				} else {
					yield readIf('.') ? readIf('.') ? SymbolToken.TDOT : SymbolToken.DDOT : SymbolToken.DOT;
				}
			}
			case ',' -> SymbolToken.COMMA;
			case '(' -> SymbolToken.LP;
			case ')' -> SymbolToken.RP;
			case '[' -> SymbolToken.LS;
			case ']' -> SymbolToken.RS;
			case '{' -> SymbolToken.LC;
			case '}' -> SymbolToken.RC;
			case '=' -> readIf('>') ? SymbolToken.ARROW : readIf('=') ? readIf('=') ? SymbolToken.SEQ : SymbolToken.EQ : SymbolToken.SET;
			case '+' -> readIf('=') ? SymbolToken.ADD_SET : readIf('+') ? SymbolToken.ADD1 : SymbolToken.ADD;
			case '-' -> readIf('=') ? SymbolToken.SUB_SET : readIf('-') ? SymbolToken.SUB1 : SymbolToken.SUB;
			case '*' -> readIf('=') ? SymbolToken.MUL_SET : readIf('*') ? SymbolToken.POW : SymbolToken.MUL;
			case '/' -> readIf('=') ? SymbolToken.DIV_SET : SymbolToken.DIV;
			case '%' -> readIf('=') ? SymbolToken.MOD_SET : SymbolToken.MOD;
			case '!' -> readIf('=') ? readIf('=') ? SymbolToken.SNEQ : SymbolToken.NEQ : SymbolToken.NOT;
			case '~' -> SymbolToken.BNOT;
			case '<' -> readIf('<') ? readIf('=') ? SymbolToken.LSH_SET : SymbolToken.LSH : readIf('=') ? SymbolToken.LTE : SymbolToken.LT;
			case '>' -> readIf('>') ? readIf('>') ? readIf('=') ? SymbolToken.URSH_SET : SymbolToken.URSH : readIf('=') ? SymbolToken.RSH_SET : SymbolToken.RSH : readIf('=') ? SymbolToken.GTE : SymbolToken.GT;
			case '^' -> readIf('=') ? SymbolToken.XOR_SET : SymbolToken.XOR;
			case '?' -> readIf('.') ? SymbolToken.OC : readIf('?') ? SymbolToken.NC : SymbolToken.HOOK;
			case '|' -> readIf('=') ? SymbolToken.BOR_SET : readIf('|') ? SymbolToken.OR : SymbolToken.BOR;
			case '&' -> readIf('=') ? SymbolToken.BAND_SET : readIf('&') ? SymbolToken.AND : SymbolToken.BAND;
			case ':' -> SymbolToken.COL;
			case ';' -> SymbolToken.SEMI;
			case '\'', '\"', '`' -> {
				var sb = new StringBuilder();

				while (true) {
					char c = read();

					if (c == '\\') {
						sb.append(readEscape(read()));
					} else if (c == t) {
						break;
					} else if (c == '\n' && t != '`') {
						yield error("Newline isn't allowed!");
					} else {
						sb.append(c);
					}
				}

				yield t == '`' ? templateLiteralTokenMap.computeIfAbsent(sb.toString(), TemplateLiteralToken::new) : stringTokenCache.computeIfAbsent(sb.toString(), StringToken::of);
			}
			default -> {
				if (isDigit(t)) {
					yield readNumber();
				} else if (isName(t)) {
					int p = pos - 1;
					int len = 1;

					while (true) {
						char c = peek(1);

						if (isName(c) || isDigit(c)) {
							len++;
							read();
						} else {
							break;
						}
					}

					var nameStr = new String(input, p, len);
					var k = KeywordToken.MAP.get(nameStr);
					yield k != null ? k.toLiteralOrSelf() : new NameToken(nameStr);
				} else {
					yield error("Unexpected token: " + t);
				}
			}
		};
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
						return error("Number can't contain decimal point twice!");
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
			return error("Invalid number: " + numStr);
		}
	}

	private char readEscape(char c) {
		return switch (c) {
			case '\\' -> '\\';
			case 'n' -> '\n';
			case 't' -> '\t';
			case 'b' -> '\b';
			case 'f' -> '\f';
			case 'r' -> '\r';
			case '"' -> '"';
			case '\'' -> '\'';
			case '`' -> '`';
			// case 'v' -> '\v';
			default -> {
				error("Invalid escape character: " + c);
				yield 0;
			}
		};
	}

	public List<PositionedToken> getTokens() {
		if (tokens == null) {
			tokens = new ArrayList<>();
		} else {
			return tokens;
		}

		while (true) {
			int l = line;
			int p = pos;
			var t = readToken();

			if (t == SymbolToken.EOF) {
				return tokens;
			} else {
				tokens.add(new PositionedToken(t, new TokenPos(tokenSource, l, p)));
			}
		}
	}

	@Override
	public String toString() {
		return getTokens().toString();
	}
}
