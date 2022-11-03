package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.RootScope;
import dev.latvian.apps.ichor.js.ContextJS;
import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.js.ParserJS;
import dev.latvian.apps.ichor.js.TokenStreamJS;
import dev.latvian.apps.ichor.test.TestConsole;
import dev.latvian.apps.ichor.util.AssignType;
import dev.latvian.apps.ichor.util.ConsoleDebugger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

// @Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class InterpreterTests {
	private static void testInterpreter(String input, boolean debug, String... match) {
		var matchStr = Arrays.asList(match);
		System.out.println("--- Interpreter Test ---");
		System.out.println("Input:");
		var lines = input.split("\n");

		for (int i = 0; i < lines.length; i++) {
			System.out.printf("%02d | %s%n", i + 1, lines[i]);
		}

		System.out.println("Expected: " + matchStr);
		var cx = new ContextJS();
		cx.debugger = new ConsoleDebugger();

		if (debug) {
			cx.setProperty("debug", debug);
		}

		var tokenStream = new TokenStreamJS(input);
		var tokens = tokenStream.getTokens();
		var parser = new ParserJS(cx, tokens);
		var ast = parser.parse();
		var astStr = ast.toString();
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
				  x *= 2;
				}
								
				print(x);
				""", true, "256.0");
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
				  	y *= x;
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
				""", "24.5");
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
				""", "24.5");
	}

	@Test
	public void defaultParams() {
		testInterpreter("""
				const a = (x, y = 'Y', z = 'Z') => {
				  print(String(x) + " : " + String(y) + " : " + String(z));
				};
								
				a('a', 'b', 'c');
				a('a', 'b');
				a('a');
				""", "a : b : c", "a : b : Z", "a : Y : Z");
	}

	@Test
	public void classes() {
		testInterpreter("""
				class TestParent {
				  constructor(param) {
				    this.param = param;
				  }
								
				  printTest(y) {
				    print(y);
				  }
				}
								
				class Test extends TestParent {
				  constructor(x, y = 10, z = 30) {
				    super(x);
				  }
				  
				  printTest2(y) {
				    printTest(y);
				  }
				}
								
				let t = new Test();
				t.printTest2('Hi');
				""", "Hi");
	}

	@Test
	public void propertyTest() {
		testInterpreter("""
				print('A');
				print('B');
				print('C');
				print(print.lastLine);
				""", "A", "B", "C", "C");
	}

	@Test
	public void stringProto() {
		testInterpreter("""
				print('Hello'.length);
				print('Hello'.charAt(0));
				""", "5", "H");
	}

	@Test
	public void emptyArray() {
		testInterpreter("""
				let empty = [];
				print(empty.length);
				""", "0");
	}

	@Test
	public void arrays() {
		testInterpreter("""
				let a = [1, 2, 'Hi'];
				print(a.length);
				print(a[0]);
				print(a[2]);
				""", "3", "1.0", "Hi");
	}
}
