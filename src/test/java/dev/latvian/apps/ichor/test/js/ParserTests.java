package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.TokenSource;
import dev.latvian.apps.ichor.js.ContextJS;
import dev.latvian.apps.ichor.js.ParserJS;
import dev.latvian.apps.ichor.js.TokenStreamJS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParserTests {
	private static void testParserAst(String input, String match) {
		System.out.println("--- Parser Test ---");
		System.out.println("Input: " + input);
		System.out.println("Expected: " + match);
		var cx = new ContextJS();
		var tokenStream = new TokenStreamJS(new TokenSource.Named("<parser test>"), input);
		var tokens = tokenStream.getTokens();
		var parser = new ParserJS(cx, tokens);
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
