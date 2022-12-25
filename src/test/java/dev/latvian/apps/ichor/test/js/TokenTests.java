package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.error.TokenStreamError;
import dev.latvian.apps.ichor.js.ContextJS;
import dev.latvian.apps.ichor.js.KeywordTokenJS;
import dev.latvian.apps.ichor.js.SymbolTokenJS;
import dev.latvian.apps.ichor.js.TokenStreamJS;
import dev.latvian.apps.ichor.token.BooleanToken;
import dev.latvian.apps.ichor.token.DoubleToken;
import dev.latvian.apps.ichor.token.NameToken;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.util.NamedTokenSource;
import dev.latvian.apps.ichor.util.PrintWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;

@Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TokenTests {
	private static void testTokenStream(String input, Object... match) {
		System.out.println("--- Token Test ---");
		System.out.println("Input: " + input);
		var cx = new ContextJS();
		var tokenStream = new TokenStreamJS(cx, new NamedTokenSource(""), input);

		Token[] matchTokens = new Token[match.length];

		for (int i = 0; i < match.length; i++) {
			if (match[i] instanceof Token token) {
				matchTokens[i] = token;
			} else if (match[i] instanceof Number number) {
				matchTokens[i] = DoubleToken.of(number.doubleValue());
			} else {
				matchTokens[i] = tokenStream.makeString(match[i].toString());
			}
		}

		var current = tokenStream.getRootToken();

		if (!current.exists()) {
			throw new IllegalStateException("No tokens!");
		}

		var tokens = new Token[tokenStream.getTokenCount()];
		int i = 0;

		while (current.exists()) {
			tokens[i++] = current.token;
			current = current.next;
		}

		System.out.println("Expected: " + Arrays.toString(matchTokens));
		System.out.println("Parsed:   " + Arrays.toString(tokens));
		Assertions.assertArrayEquals(matchTokens, tokens);
	}

	private static void testError(Executable test) {
		Assertions.assertThrows(TokenStreamError.class, () -> {
			try {
				test.execute();
			} catch (TokenStreamError e) {
				e.printPrettyError(PrintWrapper.of(System.err));
				throw e;
			}
		});
	}

	@Test
	public void numberInt() {
		testTokenStream("4", 4);
	}

	@Test
	public void numberDec() {
		testTokenStream("4.0", 4);
	}

	@Test
	public void stringDouble() {
		testTokenStream("\"Hello!\"", "Hello!");
	}

	@Test
	public void stringSingle() {
		testTokenStream("'Hello!'", "Hello!");
	}

	@Test
	public void symbols() {
		testTokenStream("+ - * /", SymbolTokenJS.ADD, SymbolTokenJS.SUB, SymbolTokenJS.MUL, SymbolTokenJS.DIV);
	}

	@Test
	public void complexSymbols() {
		testTokenStream("+ ++ >>> ??", SymbolTokenJS.ADD, SymbolTokenJS.ADD1, SymbolTokenJS.URSH, SymbolTokenJS.NC);
	}

	@Test
	public void equation() {
		testTokenStream("-3 +   4.0 * 3.0", SymbolTokenJS.SUB, 3, SymbolTokenJS.ADD, 4, SymbolTokenJS.MUL, 3);
	}

	@Test
	public void dot() {
		testTokenStream(". .. ... .3", SymbolTokenJS.DOT, SymbolTokenJS.DDOT, SymbolTokenJS.TDOT, 0.3);
	}

	@Test
	public void var() {
		testTokenStream("let x = 20;", KeywordTokenJS.LET, new NameToken("x"), SymbolTokenJS.SET, 20, SymbolTokenJS.SEMI);
	}

	@Test
	public void script() {
		testTokenStream("""
						    let x = 4.444;
						    
						    while (true) {
						      if (++x >= 10) {
						        break;
						      }
						      
						      console.print("X: " + x)
						    }
						""",
				KeywordTokenJS.LET, new NameToken("x"), SymbolTokenJS.SET, 4.444, SymbolTokenJS.SEMI,
				KeywordTokenJS.WHILE, SymbolTokenJS.LP, BooleanToken.TRUE, SymbolTokenJS.RP, SymbolTokenJS.LC,
				KeywordTokenJS.IF, SymbolTokenJS.LP, SymbolTokenJS.ADD1, new NameToken("x"), SymbolTokenJS.GTE, 10, SymbolTokenJS.RP, SymbolTokenJS.LC,
				KeywordTokenJS.BREAK, SymbolTokenJS.SEMI,
				SymbolTokenJS.RC,
				new NameToken("console"), SymbolTokenJS.DOT, new NameToken("print"), SymbolTokenJS.LP, "X: ", SymbolTokenJS.ADD, new NameToken("x"), SymbolTokenJS.RP,
				SymbolTokenJS.RC
		);
	}

	@Test
	public void brackets() {
		testTokenStream("{}", SymbolTokenJS.LC, SymbolTokenJS.RC);
	}

	@Test
	public void bracketsOpen() {
		testError(() -> testTokenStream("{", SymbolTokenJS.LC));
	}

	@Test
	public void bracketsClosed() {
		testError(() -> testTokenStream("}", SymbolTokenJS.RC));
	}

	@Test
	public void bracketsMismatch() {
		testError(() -> testTokenStream("{]", SymbolTokenJS.LC, SymbolTokenJS.RS));
	}

	@Test
	public void templateLiteralString() {
		testTokenStream("`Entity has spawned`",
				SymbolTokenJS.TEMPLATE_LITERAL,
				"Entity has spawned",
				SymbolTokenJS.TEMPLATE_LITERAL
		);
	}

	@Test
	public void templateLiteralExpression() {
		testTokenStream("`Entity has spawned at X: ${location.pos.x}, Y: ${location.pos.y}`",
				SymbolTokenJS.TEMPLATE_LITERAL,
				"Entity has spawned at X: ",
				SymbolTokenJS.TEMPLATE_LITERAL_VAR,
				new NameToken("location"),
				SymbolTokenJS.DOT,
				new NameToken("pos"),
				SymbolTokenJS.DOT,
				new NameToken("x"),
				SymbolTokenJS.RC,
				", Y: ",
				SymbolTokenJS.TEMPLATE_LITERAL_VAR,
				new NameToken("location"),
				SymbolTokenJS.DOT,
				new NameToken("pos"),
				SymbolTokenJS.DOT,
				new NameToken("y"),
				SymbolTokenJS.RC,
				SymbolTokenJS.TEMPLATE_LITERAL
		);
	}
}
