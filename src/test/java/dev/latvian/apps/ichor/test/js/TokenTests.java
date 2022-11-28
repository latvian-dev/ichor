package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.error.TokenStreamError;
import dev.latvian.apps.ichor.js.TokenStreamJS;
import dev.latvian.apps.ichor.token.BooleanToken;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.NameToken;
import dev.latvian.apps.ichor.token.NumberToken;
import dev.latvian.apps.ichor.token.StringToken;
import dev.latvian.apps.ichor.token.SymbolToken;
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
	private static void testTokenStream(String input, Token... match) {
		System.out.println("--- Token Test ---");
		System.out.println("Input: " + input);
		var tokenStream = new TokenStreamJS(new NamedTokenSource(""), input);
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

		System.out.println("Expected: " + Arrays.toString(match));
		System.out.println("Parsed:   " + Arrays.toString(tokens));
		Assertions.assertArrayEquals(match, tokens);
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
		testTokenStream("4", NumberToken.of(4));
	}

	@Test
	public void numberDec() {
		testTokenStream("4.0", NumberToken.of(4));
	}

	@Test
	public void stringDouble() {
		testTokenStream("\"Hello!\"", StringToken.of("Hello!"));
	}

	@Test
	public void stringSingle() {
		testTokenStream("'Hello!'", StringToken.of("Hello!"));
	}

	@Test
	public void symbols() {
		testTokenStream("+ - * /", SymbolToken.ADD, SymbolToken.SUB, SymbolToken.MUL, SymbolToken.DIV);
	}

	@Test
	public void complexSymbols() {
		testTokenStream("+ ++ >>> ??", SymbolToken.ADD, SymbolToken.ADD1, SymbolToken.URSH, SymbolToken.NC);
	}

	@Test
	public void equation() {
		testTokenStream("-3 +   4.0 * 3.0", SymbolToken.SUB, NumberToken.of(3), SymbolToken.ADD, NumberToken.of(4), SymbolToken.MUL, NumberToken.of(3));
	}

	@Test
	public void dot() {
		testTokenStream(". .. ... .3", SymbolToken.DOT, SymbolToken.DDOT, SymbolToken.TDOT, NumberToken.of(0.3));
	}

	@Test
	public void var() {
		testTokenStream("let x = 20;", KeywordToken.LET, new NameToken("x"), SymbolToken.SET, NumberToken.of(20), SymbolToken.SEMI);
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
				KeywordToken.LET, new NameToken("x"), SymbolToken.SET, NumberToken.of(4.444), SymbolToken.SEMI,
				KeywordToken.WHILE, SymbolToken.LP, BooleanToken.TRUE, SymbolToken.RP, SymbolToken.LC,
				KeywordToken.IF, SymbolToken.LP, SymbolToken.ADD1, new NameToken("x"), SymbolToken.GTE, NumberToken.of(10), SymbolToken.RP, SymbolToken.LC,
				KeywordToken.BREAK, SymbolToken.SEMI,
				SymbolToken.RC,
				new NameToken("console"), SymbolToken.DOT, new NameToken("print"), SymbolToken.LP, StringToken.of("X: "), SymbolToken.ADD, new NameToken("x"), SymbolToken.RP,
				SymbolToken.RC
		);
	}

	@Test
	public void brackets() {
		testTokenStream("{}", SymbolToken.LC, SymbolToken.RC);
	}

	@Test
	public void bracketsOpen() {
		testError(() -> testTokenStream("{", SymbolToken.LC));
	}

	@Test
	public void bracketsClosed() {
		testError(() -> testTokenStream("}", SymbolToken.RC));
	}

	@Test
	public void bracketsMismatch() {
		testError(() -> testTokenStream("{]", SymbolToken.LC, SymbolToken.RS));
	}

	@Test
	public void templateLiteralString() {
		testTokenStream("`Entity has spawned`",
				SymbolToken.TEMPLATE_LITERAL,
				StringToken.of("Entity has spawned"),
				SymbolToken.TEMPLATE_LITERAL
		);
	}

	@Test
	public void templateLiteralExpression() {
		testTokenStream("`Entity has spawned at X: ${location.pos.x}, Y: ${location.pos.y}`",
				SymbolToken.TEMPLATE_LITERAL,
				StringToken.of("Entity has spawned at X: "),
				SymbolToken.TEMPLATE_LITERAL_VAR,
				new NameToken("location"),
				SymbolToken.DOT,
				new NameToken("pos"),
				SymbolToken.DOT,
				new NameToken("x"),
				SymbolToken.RC,
				StringToken.of(", Y: "),
				SymbolToken.TEMPLATE_LITERAL_VAR,
				new NameToken("location"),
				SymbolToken.DOT,
				new NameToken("pos"),
				SymbolToken.DOT,
				new NameToken("y"),
				SymbolToken.RC,
				SymbolToken.TEMPLATE_LITERAL
		);
	}
}
