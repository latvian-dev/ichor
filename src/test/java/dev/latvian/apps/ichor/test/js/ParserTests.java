package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.js.ContextJS;
import dev.latvian.apps.ichor.js.ParserJS;
import dev.latvian.apps.ichor.js.TokenStreamJS;
import dev.latvian.apps.ichor.util.NamedTokenSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ParserTests {
	public static void testParserAst(String filename, String input, String match) {
		System.out.println("--- Parser Test ---");
		System.out.println("Input: " + input);
		System.out.println("Expected: " + match);
		var cx = new ContextJS();
		var tokenStream = new TokenStreamJS(new NamedTokenSource(filename), input);
		var tokens = tokenStream.getTokens();
		var parser = new ParserJS(cx, tokens);
		var ast = parser.parse();
		var astStr = ast.toString();
		System.out.println("Parsed:   " + astStr);

		if (!match.equals("*")) {
			Assertions.assertEquals(match, astStr);
		}
	}

	public static void testParserAst(String input, String match) {
		testParserAst("<parser test>", input, match);
	}

	@Test
	public void number() {
		testParserAst("const x = 4.0;", "const x=4.0");
	}
}
