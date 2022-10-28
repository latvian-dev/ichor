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
		var result = parser.parse();
		var resultStr = result.toString();
		System.out.println("Parsed:   " + resultStr);
		Assertions.assertEquals(match, resultStr);
	}

	@Test
	public void number() {
		testParser("const x = 4.0;", "{x=4.0;}");
	}
}
