package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.RootScope;
import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.parser.Parser;
import dev.latvian.apps.ichor.token.TokenStream;
import dev.latvian.apps.ichor.util.AssignType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Arrays;

@Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class InterpreterTests {
	private static void testInterpreter(String input, boolean debugContext, String... match) {
		var matchStr = Arrays.asList(match);
		System.out.println("--- Interpreter Test ---");
		System.out.println("Input: " + input);
		System.out.println("Expected: " + matchStr);
		var tokenStream = new TokenStream(input);
		var tokens = tokenStream.getTokens();
		var parser = new Parser(tokens);
		var ast = parser.parse();
		var astStr = ast.toString();
		var cx = new Context();
		cx.debug = debugContext;
		var console = new TestConsole(System.out, new ArrayList<>());
		var rootScope = new RootScope(cx);
		rootScope.addSafeClasses();
		rootScope.declareMember("print", console, AssignType.IMMUTABLE);
		System.out.println("Parsed:   " + astStr);

		try {
			ast.interpret(rootScope);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		System.out.println("Returned: " + console.output().toString());
		Assertions.assertEquals(matchStr, console.output());
	}

	private static void testInterpreter(String input, String... match) {
		testInterpreter(input, false, match);
	}

	@Test
	public void numberOps() {
		testInterpreter("let x = 4.0; x++; ++x; const y = x; print(y);", "6.0");
	}

	@Test
	public void whileLoop() {
		testInterpreter("""
				let x = 2.0, i = 0;
				 
				while(++i < 8) {
				  x = x * 2;
				}
								
				print(x);
				""", "256.0");
	}

	@Test
	public void print() {
		testInterpreter("""
				print("Hello")
				""", "Hello");
	}

	@Test
	public void functionPrintDash() {
		testInterpreter("""
				function test(x) {
				  print('- ' + x);
				}
								
				test("Hello");
				""", "- Hello");
	}

	@Test
	public void functionPrintPowWithComment() {
		testInterpreter("""
				function printPower(x, pow) {
				  var y = 1;
								
				  while(--pow >= 0) {
				  	y = y * x;
				  	// print(y);
				  }
				  
				  print(y);
				}
								
				printPower(2, 3);
				""", "8.0");
	}

	@Test
	public void nestedFunction() {
		testInterpreter("""
				function a(x) {
				  function b(y) {
				    return y * y;
				  }
								
				  return b(x) + b(x);
				}
								
				let z = a(3.5);
				print(z);
				""", true, "24.5");
	}

	@Test
	public void recursionFib() {
		testInterpreter("""
				const fib = function(n) {
				  if (n <= 1) {
				    return n;
				  }
								
				  return fib(n - 1) + fib(n - 2);
				};
								
				let z = fib(10);
				print(z);
				""", "55.0");
	}

	@Test
	public void recursionOddEven() {
		testInterpreter("""
				function isOdd(n) {
				  if (n == 0) return false;
				  return isEven(n - 1);
				}
								
				function isEven(n) {
				  if (n == 0) return true;
				  return isOdd(n - 1);
				}
								
				print(isOdd(7));
				print(isEven(7));
				""", "true", "false");
	}

	@Test
	public void redeclaration() {
		testInterpreter("""
				var x = 10;
				print(x);
								
				{
				  print(x);
				  var x = 20;
				  print(x);
				}
								
				print(x);
				""", "10.0", "10.0", "20.0", "10.0");
	}

	@Test
	public void numberProtoAccess() {
		testInterpreter("""
				print(Number.MAX_SAFE_INTEGER);
				""", Double.toString(NumberJS.MAX_SAFE_INTEGER));
	}

	@Test
	public void nestedArrowFunction() {
		testInterpreter("""
				const a = x => {
				  const b = y => { return y * y; };
				  return b(x) + b(x);
				};
								
				let z = a(3.5);
				print(z);
				""", true, "24.5");
	}
}
