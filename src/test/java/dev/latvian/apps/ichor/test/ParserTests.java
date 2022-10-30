package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Interpreter;
import dev.latvian.apps.ichor.parser.Parser;
import dev.latvian.apps.ichor.token.TokenStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

	private static void testInterpreter(String input, Object match) {
		System.out.println("--- Interpreter Test ---");
		System.out.println("Input: " + input);
		System.out.println("Expected: " + match);
		var tokenStream = new TokenStream(input);
		var tokens = tokenStream.getTokens();
		var parser = new Parser(tokens);
		var ast = parser.parse();
		var astStr = ast.toString();
		var cx = new Context();
		var interpreter = new Interpreter(cx);
		System.out.println("Parsed:   " + astStr);
		ast.interpret(interpreter);
		System.out.println("Returned: " + interpreter.returnValue);
		Assertions.assertEquals(match, interpreter.returnValue);
	}

	@Test
	public void number() {
		testParserAst("const x = 4.0;", "const x=4.0");
	}

	@Test
	public void numberOps() {
		testInterpreter("let x = 4.0; x++; ++x; const y = x; return y;", 6.0);
	}

	@Test
	public void whileLoop() {
		testInterpreter("""
				let x = 2.0, i = 0;
				 
				while(++i < 8) {
				  x = x * 2;
				}
								
				return x;
				""", 256.0);
	}
}
