package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.error.TokenStreamError;
import dev.latvian.apps.ichor.js.ContextJS;
import dev.latvian.apps.ichor.js.TokenStreamJS;
import dev.latvian.apps.ichor.token.IdentifierToken;
import dev.latvian.apps.ichor.util.NamedTokenSource;
import dev.latvian.apps.ichor.util.PrintWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;

import static dev.latvian.apps.ichor.js.KeywordTokenJS.*;
import static dev.latvian.apps.ichor.js.SymbolTokenJS.SET;
import static dev.latvian.apps.ichor.js.SymbolTokenJS.*;

@Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class TokenTests {
	private static IdentifierToken n(String name) {
		return new IdentifierToken(name);
	}

	private static void testTokenStream(String input, Object... match) {
		System.out.println("--- Token Test ---");
		System.out.println("Input: " + input);
		var cx = new ContextJS();
		var tokenStream = new TokenStreamJS(cx, new NamedTokenSource(""), input);

		var current = tokenStream.getRootToken();

		if (!current.exists()) {
			throw new IllegalStateException("No tokens!");
		}

		var tokens = new Object[tokenStream.getTokenCount()];
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
		testTokenStream("4", 4);
	}

	@Test
	public void numberDec() {
		testTokenStream("4.0", 4.0);
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
		testTokenStream("x + x - x * x / x", n("x"), ADD, n("x"), SUB, n("x"), MUL, n("x"), DIV, n("x"));
	}

	@Test
	public void complexSymbols() {
		testTokenStream("+ ++ >>> ??", ADD, ADD1, URSH, NC);
	}

	@Test
	public void equation() {
		testTokenStream("-3 +   4.0 * 3.0", SUB, 3, ADD, 4.0, MUL, 3.0);
	}

	@Test
	public void dot() {
		testTokenStream(". .. ... .3", DOT, DDOT, TDOT, 0.3);
	}

	@Test
	public void var() {
		testTokenStream("let x = 20;", LET, n("x"), SET, 20, SEMI);
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
				LET, n("x"), SET, 4.444, SEMI,
				WHILE, LP, true, RP, LC,
				IF, LP, ADD1, n("x"), GTE, 10, RP, LC,
				BREAK, SEMI,
				RC,
				n("console"), DOT, n("print"), LP, "X: ", ADD, n("x"), RP,
				RC
		);
	}

	@Test
	public void brackets() {
		testTokenStream("{}", LC, RC);
	}

	@Test
	public void bracketsOpen() {
		testError(() -> testTokenStream("{", LC));
	}

	@Test
	public void bracketsClosed() {
		testError(() -> testTokenStream("}", RC));
	}

	@Test
	public void bracketsMismatch() {
		testError(() -> testTokenStream("{]", LC, RS));
	}

	@Test
	public void templateLiteralString() {
		testTokenStream("`Entity has spawned`",
				TEMPLATE_LITERAL,
				"Entity has spawned",
				TEMPLATE_LITERAL
		);
	}

	@Test
	public void templateLiteralExpression() {
		testTokenStream("`Entity has spawned at X: ${location.pos.x}, Y: ${location.pos.y}`",
				TEMPLATE_LITERAL,
				"Entity has spawned at X: ",
				TEMPLATE_LITERAL_VAR,
				n("location"),
				DOT,
				n("pos"),
				DOT,
				n("x"),
				RC,
				", Y: ",
				TEMPLATE_LITERAL_VAR,
				n("location"),
				DOT,
				n("pos"),
				DOT,
				n("y"),
				RC,
				TEMPLATE_LITERAL
		);
	}
}
