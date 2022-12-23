package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.ast.AstAppendable;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
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
		System.out.println("Input: " + (match.equals("*") ? "<skipped>" : input));
		System.out.println("Expected: " + match);
		var cx = new ContextJS();
		var tokenStream = new TokenStreamJS(new NamedTokenSource(filename), input);
		var rootToken = tokenStream.getRootToken();
		var parser = new ParserJS(cx, rootToken);
		var ast = parser.parse();

		var sb = new AstStringBuilder();
		((AstAppendable) ast).append(sb);

		if (!match.equals("*")) {
			var astStr = sb.toString();
			System.out.println("Parsed:   " + astStr);
			Assertions.assertEquals(match, astStr);
		} else {
			System.out.println("Parsed:   <skipped>");
		}
	}

	public static void testParserAst(String input, String match) {
		testParserAst("", input, match);
	}

	@Test
	public void number() {
		testParserAst("const x = 4.0;", "const x=4.0;");
	}

	@Test
	public void confusingArg() {
		testParserAst("""
				let a = {b: 1, c: true}
				let d = true
				print(a.b = (a.c) ? d : false)
				""", "let a={b:1.0,c:true};let d=true;print(a.b=(a.c?d:false));");
	}
}
