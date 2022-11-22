package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.RootScope;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.exit.ScopeExit;
import dev.latvian.apps.ichor.js.ContextJS;
import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.js.ParserJS;
import dev.latvian.apps.ichor.js.TokenStreamJS;
import dev.latvian.apps.ichor.test.ReflectionExample;
import dev.latvian.apps.ichor.test.TestConsole;
import dev.latvian.apps.ichor.util.AssignType;
import dev.latvian.apps.ichor.util.ConsoleDebugger;
import dev.latvian.apps.ichor.util.EmptyArrays;
import dev.latvian.apps.ichor.util.NamedTokenSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

// @Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class InterpreterTests {
	private static void printLines(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			System.out.printf("%02d | %s%n", i + 1, lines.get(i));
		}
	}

	public static void testInterpreter(String filename, String input, Consumer<RootScope> rootScopeCallback, String match) {
		var matchStr = Arrays.asList(match.split("\n"));
		System.out.println("--- Interpreter Test ---");
		System.out.println("Input:");
		printLines(Arrays.asList(input.split("\n")));
		System.out.println();
		System.out.println("Expected:");
		printLines(matchStr);
		System.out.println();

		var cx = new ContextJS();
		var rootScope = new RootScope(cx);
		rootScope.addSafeClasses();
		var console = new TestConsole(System.out, new ArrayList<>());
		rootScope.declareMember("print", console, AssignType.IMMUTABLE);
		rootScopeCallback.accept(rootScope);
		cx.debugger = new ConsoleDebugger();

		var tokenStream = new TokenStreamJS(new NamedTokenSource(filename), input);
		tokenStream.timeout(1500L);
		var tokens = tokenStream.getTokens();
		var parser = new ParserJS(cx, tokens);
		var ast = parser.parse();
		var astStr = ast.toString();

		System.out.println("Parsed:");
		System.out.println(astStr);
		System.out.println();

		try {
			ast.interpret(rootScope);
		} catch (ScopeExit ex) {
			throw new ScriptError(ex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		System.out.println("Returned:");
		printLines(console.output());
		Assertions.assertEquals(matchStr, console.output());
	}

	public static void testInterpreter(String input, Consumer<RootScope> rootScopeCallback, String match) {
		testInterpreter("<interpreter test>", input, rootScopeCallback, match);
	}

	public static void testInterpreter(String input, String match) {
		testInterpreter(input, EmptyArrays.consumer(), match);
	}

	@Test
	public void numberOps() {
		testInterpreter("let x = 4.0; x++; ++x; const y = x; print(y);", "6.0");
	}

	@Test
	public void whileLoop() {
		testInterpreter("""
				let x = 2.0, i = 0
				 
				while(++i < 8) {
				  x *= 2
				}
								
				print(x)
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
				  print('- ' + x)
				}
								
				test("Hello");
				""", "- Hello");
	}

	@Test
	public void functionPrintPowWithComment() {
		testInterpreter("""
				function printPower(x, pow) {
				  var y = 1
								
				  while(--pow >= 0) {
				  	y *= x
				  	// print(y)
				  }
				  
				  print(y)
				}
								
				printPower(2, 3)
				""", "8.0");
	}

	@Test
	public void nestedFunction() {
		testInterpreter("""
				function a(x) {
				  function b(y) {
				    return y * y
				  }
								
				  return b(x) + b(x)
				}
								
				let z = a(3.5)
				print(z)
				""", "24.5");
	}

	@Test
	public void recursionFib() {
		testInterpreter("""
				const fib = function(n) {
				  if (n <= 1) {
				    return n
				  }
								
				  return fib(n - 1) + fib(n - 2)
				}
								
				let z = fib(10)
				print(z);
				""", "55.0");
	}

	@Test
	public void recursionOddEven() {
		testInterpreter("""
				function isOdd(n) {
				  if (n == 0) return false
				  return isEven(n - 1)
				}
								
				function isEven(n) {
				  if (n == 0) return true
				  return isOdd(n - 1)
				}
								
				print(isOdd(7))
				print(isEven(7))
				""", """
				true
				false
				""");
	}

	@Test
	public void redeclaration() {
		testInterpreter("""
				var x = 10
				print(x)
								
				{
				  print(x)
				  var x = 20
				  print(x)
				}
								
				print(x)
				""", """
				10.0
				10.0
				20.0
				10.0
				""");
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
				  const b = y => { return y * y; }
				  return b(x) * 2
				}
								
				let z = a(3.5)
				print(z)
				""", "24.5");
	}

	@Test
	public void defaultParams() {
		testInterpreter("""
				const a = (x, y = 'Y', z = 'Z') => {
				  print(String(x) + " : " + String(y) + " : " + String(z))
				}
								
				a('a', 'b', 'c')
				a('a', 'b')
				a('a')
				""", """
				a : b : c
				a : b : Z
				a : Y : Z
				""");
	}

	// @Test
	public void classes() {
		testInterpreter("""
				class TestParent {
				  constructor(param) {
				    this.param = param
				  }
								
				  printTest(y) {
				    print(y)
				  }
				}
								
				let t1 = new TestParent(-439)
				t1.printTest('Hi 1')
								
				class Test extends TestParent {
				  constructor(x, y = 10, z = 30) {
				    super(x)
				  }
				  
				  printTest2(y) {
				    printTest(y)
				  }
				}
								
				let t2 = new Test(9)
				t2.printTest2('Hi 2')
				""", """
				Hi 1
				Hi 2
				""");
	}

	@Test
	public void propertyTest() {
		testInterpreter("""
				print('A')
				print('B')
				print('C')
				print(print.lastLine)
				""", """
				A
				B
				C
				C
				""");
	}

	@Test
	public void stringProto() {
		testInterpreter("""
				print('Hello'.length)
				print('Hello'.charAt(0))
				print(String(4))
				print(new String(true))
				""", """
				5
				H
				4.0
				true
				""");
	}

	@Test
	public void emptyArray() {
		testInterpreter("""
				let empty = []
				print(empty.length)
				""", "0");
	}

	@Test
	public void arrays() {
		testInterpreter("""
				let a = [1, 2, 'Hi']
				print(a.length)
				print(a[0])
				print(a[2])
				""", """
				3
				1.0
				Hi
				""");
	}

	@Test
	public void comments() {
		testInterpreter("""
				// before
				print('A')
				/* block comment
				print('B')
				*/
				print('C')
				""", """
				A
				C
				""");
	}

	@Test
	public void reflection() {
		testInterpreter("""
				print(ref.publicField)
				ref.publicField = 40
				print(ref.publicField)
				ref.sout('Hello')
				ref.sout(7)
				ref.soutNum('8')
				print(ref.class.name)
				print(ref.class.class.name)
				""", scope -> scope.declareMember("ref", new ReflectionExample(), AssignType.IMMUTABLE), """
				30
				40
				dev.latvian.apps.ichor.test.ReflectionExample
				java.lang.Class
				""");
	}

	@Test
	public void ternary() {
		testInterpreter("""
				let x = 40
				let y = 60
				print()
				// before
				print('A')
				/* block comment
				print('B')
				*/
				print('C')
				""", """
				A
				C
				""");
	}

	@Test
	public void templateLiteralString() {
		testInterpreter("""
				print(`Hello`)
				""", "Hello");
	}

	@Test
	public void templateLiteralExpression() {
		testInterpreter("""
				let x = 10
				let y = 20
				print(`Hello ${x} + ${y} = ${x + y}`)
				""", "Hello 10.0 + 20.0 = 30.0");
	}

	@Test
	public void nestedTemplateLiteralExpression() {
		testInterpreter("""
				let x = 10
				let y = 20
				print(`Hello ${x} + ${y} = ${true ? `${x + y}` : 'impossible'}`)
				""", "Hello 10.0 + 20.0 = 30.0");
	}

	@Test
	public void forIndex() {
		testInterpreter("""
				const arr = ['a', 'b', 'c']
								
				for (let i = 0; i < arr.length; i++) {
				  print(`${i}: ${arr[i]}`)
				}
				""", """
				0.0: a
				1.0: b
				2.0: c
				""");
	}

	@Test
	public void forOfArr() {
		testInterpreter("""
				const arr = ['a', 'b', 'c']
								
				for (const x of arr) {
				  print(x)
				}
				""", """
				a
				b
				c
				""");
	}

	@Test
	public void forInArr() {
		testInterpreter("""
				const arr = ['a', 'b', 'c']
								
				for (x in arr) {
				  print(x)
				}
				""", """
				0
				1
				2
				""");
	}

	@Test
	public void forOfObj() {
		testInterpreter("""
				const obj = {a: 'X', b: 'Y', c: 'Z'}
								
				for (const x of obj) {
				  print(x)
				}
				""", """
				X
				Y
				Z
				""");
	}

	@Test
	public void forInObj() {
		testInterpreter("""
				const obj = {a: 'X', b: 'Y', c: 'Z'}
								
				for (x in obj) {
				  print(x)
				}
				""", """
				a
				b
				c
				""");
	}
}
