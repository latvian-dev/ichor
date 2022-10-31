package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.parser.Parser;
import dev.latvian.apps.ichor.token.TokenStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class ParserTests {
	private static void testParserAst(String input, String match) {
		System.out.println("--- Parser Test ---");
		System.out.println("Input: " + input);
		System.out.println("Expected: " + match);
		var tokenStream = new TokenStream(input);
		var tokens = tokenStream.getTokens();
		var parser = new Parser(tokens);
		var ast = parser.parse();
		var astStr = ast.toString();
		System.out.println("Parsed:   " + astStr);
		Assertions.assertEquals(match, astStr);
	}

	@Test
	public void number() {
		testParserAst("const x = 4.0;", "const x=4.0");
	}
}
