package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.LiteralToken;
import dev.latvian.apps.ichor.token.NameToken;
import dev.latvian.apps.ichor.token.NumberToken;
import dev.latvian.apps.ichor.token.PositionedToken;
import dev.latvian.apps.ichor.token.StringToken;
import dev.latvian.apps.ichor.token.SymbolToken;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.token.TokenStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TokenTests {
	private static void testTokenStream(String input, Token... match) {
		System.out.println("--- Token Test ---");
		System.out.println("Input: " + input);
		var tokenStream = new TokenStream(input);
		var tokens = tokenStream.getTokens().stream().map(PositionedToken::token).toArray(Token[]::new);
		System.out.println("Expected: " + Arrays.toString(match));
		System.out.println("Parsed:   " + Arrays.toString(tokens));
		Assertions.assertArrayEquals(match, tokens);
	}

	@Test
	public void number() {
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
				KeywordToken.WHILE, SymbolToken.LP, LiteralToken.TRUE, SymbolToken.RP, SymbolToken.LC,
				KeywordToken.IF, SymbolToken.LP, SymbolToken.ADD1, new NameToken("x"), SymbolToken.GTE, NumberToken.of(10), SymbolToken.RP, SymbolToken.LC,
				KeywordToken.BREAK, SymbolToken.SEMI,
				SymbolToken.RC,
				new NameToken("console"), SymbolToken.DOT, new NameToken("print"), SymbolToken.LP, new StringToken("X: "), SymbolToken.ADD, new NameToken("x"), SymbolToken.RP,
				SymbolToken.RC
		);
	}
}
