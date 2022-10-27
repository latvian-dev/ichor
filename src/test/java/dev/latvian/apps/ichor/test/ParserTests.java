package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.parser.Parser;
import dev.latvian.apps.ichor.token.TokenStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParserTests {
	private static void testParser(String input, String match) {
		System.out.println("--- Parser Test ---");
		System.out.println("Input: " + input);
		System.out.println("Expected: " + match);
		var tokenStream = new TokenStream(input);
		var tokens = tokenStream.getTokens();
		var parser = new Parser(tokens);
		var stmts = parser.parse();
		System.out.println("Parsed:   " + stmts);
		Assertions.assertEquals(match, stmts.toString());
	}

	@Test
	public void number() {
		testParser("var x = 4.0;", "[4.0]");
	}
}
