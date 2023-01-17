package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.RootScope;
import dev.latvian.apps.ichor.exit.ScopeExit;
import dev.latvian.apps.ichor.js.ContextJS;
import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.js.ParserJS;
import dev.latvian.apps.ichor.js.TokenStreamJS;
import dev.latvian.apps.ichor.test.ReflectionExample;
import dev.latvian.apps.ichor.test.TestConsole;
import dev.latvian.apps.ichor.util.ConsoleDebugger;
import dev.latvian.apps.ichor.util.Empty;
import dev.latvian.apps.ichor.util.NamedTokenSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

// @Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class InterpreterTests {
	private static void printLines(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			System.out.printf("%02d | %s%n", i + 1, lines.get(i));
		}
	}

	public static void testInterpreter(String filename, String input, Consumer<RootScope> rootScopeCallback, String match) {
		var matchStr = Arrays.asList(match.split("\n"));
		System.out.println("--- Interpreter Test ---");
		System.out.println();
		System.out.println("Input:");
		printLines(Arrays.asList(input.split("\n")));
		System.out.println();
		System.out.println("Expected:");
		printLines(matchStr);
		System.out.println();

		var cx = new ContextJS();
		cx.debugger = ConsoleDebugger.INSTANCE;
		cx.setProperty(Context.INTERPRETING_TIMEOUT, 1500L);
		cx.setProperty(Context.TOKEN_STREAM_TIMEOUT, filename.isEmpty() ? 1500L : 0L);

		var rootScope = new RootScope(cx);
		rootScope.addSafeClasses();
		var console = new TestConsole(System.out);
		rootScope.addImmutable("console", console);
		rootScopeCallback.accept(rootScope);

		var tokenStream = new TokenStreamJS(cx, new NamedTokenSource(filename), input);
		var rootToken = tokenStream.getRootToken();
		var parser = new ParserJS(cx, rootToken);
		var ast = parser.parse();
		var astStr = ast.toString();

		System.out.println("Parsed:");
		System.out.println(astStr);
		System.out.println();

		try {
			ast.interpretSafe(cx, rootScope);
		} catch (ScopeExit ex) {
			throw ex;
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		System.out.println();
		System.out.println("Returned:");
		printLines(console.output);
		Assertions.assertEquals(matchStr, console.output);
	}

	public static void testInterpreter(String input, Consumer<RootScope> rootScopeCallback, String match) {
		testInterpreter("", input, rootScopeCallback, match);
	}

	public static void testInterpreter(String input, String match) {
		testInterpreter(input, Empty.consumer(), match);
	}

	@Test
	public void numberOps() {
		testInterpreter("let x = 4.0; x++; ++x; const y = x; x++; console.log(y);", "6");
	}

	@Test
	public void whileLoop() {
		testInterpreter("""
				let x = 2, i = 0
				 
				while(++i < 8) {
				  x *= 2
				}
								
				console.log(x)
				""", "256");
	}

	@Test
	public void print() {
		testInterpreter("""
				console.log("Hello")
				""", "Hello");
	}

	@Test
	public void functionPrintDash() {
		testInterpreter("""
				function test(x) {
				  console.log('- ' + x)
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
				  	// console.log(y)
				  }
				  
				  console.log(y)
				}
								
				printPower(2, 3)
				""", "8");
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
				console.log(z)
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
				console.log(z);
				""", "55");
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
								
				console.log(isOdd(7))
				console.log(isEven(7))
				""", """
				true
				false
				""");
	}

	@Test
	public void redeclaration() {
		testInterpreter("""
				var x = 10
				console.log(x)
								
				{
				  console.log(x)
				  var x = 20
				  console.log(x)
				}
								
				console.log(x)
				""", """
				10
				10
				20
				10
				""");
	}

	@Test
	public void numberProtoAccess() {
		testInterpreter("""
				console.log(Number.MAX_SAFE_INTEGER);
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
				console.log(z)
				""", "24.5");
	}

	@Test
	public void defaultParams() {
		testInterpreter("""
				const a = (x, y = 'Y', z = 'Z') => {
				  console.log(String(x) + " : " + String(y) + " : " + String(z))
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
				    console.log(y)
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
				console.log('A')
				console.log('B')
				console.log('C')
				console.log(console.lastLine)
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
				console.log('Hello'.length)
				console.log('Hello'.charAt(0))
				console.log(String(4))
				console.log(new String(true))
				""", """
				5
				H
				4
				true
				""");
	}

	@Test
	public void emptyArray() {
		testInterpreter("""
				let empty = []
				console.log(empty.length)
				""", "0");
	}

	@Test
	public void arrays() {
		testInterpreter("""
				let a = [1, 2, 'Hi']
				console.log(a.length)
				console.log(a[0])
				console.log(a[2])
				""", """
				3
				1
				Hi
				""");
	}

	@Test
	public void arrayPrint() {
		testInterpreter("""
				console.log([1, 2.5, 'Hi'])
				""", """
				[1, 2.5, 'Hi']
				""");
	}

	@Test
	public void objectPrint() {
		testInterpreter("""
				console.log({a: 1, b: 2.5, c: 'Hi'})
				""", """
				{a: 1, b: 2.5, c: 'Hi'}
				""");
	}

	@Test
	public void comments() {
		testInterpreter("""
				// before
				console.log('A')
				/* block comment
				console.log('B')
				*/
				console.log('C')
				""", """
				A
				C
				""");
	}

	@Test
	public void reflection() {
		testInterpreter("""
				console.log(ref.publicField)
				ref.publicField = 40
				console.log(ref.publicField)
				ref.sout('Hello')
				ref.sout(7)
				ref.soutNum('8')
				console.log(ref.class.name)
				console.log(ref.class.class.name)
				""", scope -> scope.addImmutable("ref", new ReflectionExample()), """
				30
				40
				dev.latvian.apps.ichor.test.ReflectionExample
				java.lang.Class
				""");
	}

	@Test
	public void templateLiteralString() {
		testInterpreter("""
				console.log(`Hello`)
				""", "Hello");
	}

	@Test
	public void templateLiteralExpression() {
		testInterpreter("""
				let x = 10
				let y = 20
				console.log(`Hello ${x} + ${y} = ${x + y}`)
				""", "Hello 10 + 20 = 30");
	}

	@Test
	public void nestedTemplateLiteralExpression() {
		testInterpreter("""
				let x = 10
				let y = 20
				console.log(`Hello ${x} + ${y} = ${true ? `${x + y}` : 'impossible'}`)
				""", "Hello 10 + 20 = 30");
	}

	@Test
	public void forNumbers() {
		testInterpreter("""
				for (let i = 0; i < 5; i++) {
				  console.log(i)
				}
				""", """
				0
				1
				2
				3
				4
				""");
	}

	@Test
	public void forIndex() {
		testInterpreter("""
				const arr = ['a', 'b', 'c']
								
				for (let i = 0; i < arr.length; i++) {
				  console.log(`${i}: ${arr[i]}`)
				}
				""", """
				0: a
				1: b
				2: c
				""");
	}

	@Test
	public void forIndexPredef() {
		testInterpreter("""
				const arr = ['a', 'b', 'c']
				let i;
				i = 50;
								
				for (i = 0; i < arr.length; i++) {
				  console.log(`${i}: ${arr[i]}`)
				}
				""", """
				0: a
				1: b
				2: c
				""");
	}

	@Test
	public void forOfArr() {
		testInterpreter("""
				const arr = ['a', 'b', 'c']
								
				for (const x of arr) {
				  console.log(x)
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
				  console.log(x)
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
				  console.log(x)
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
				  console.log(x)
				}
				""", """
				a
				b
				c
				""");
	}

	@Test
	public void assignExpr() {
		testInterpreter("""
				let y = 28;
				let x;
				if((x = y/2) > 10)
				  console.log(x)
				""", """
				14
				""");
	}

	@Test
	public void callRef1() {
		testInterpreter("""
				const l = console.log
				l('Hi')
				""", """
				Hi
				""");
	}

	@Test
	public void callRef2() {
		testInterpreter("""
				const c = console
				const l = c.log
				l('Hi')
				""", """
				Hi
				""");
	}

	@Test
	public void callRefArrow() {
		testInterpreter("""
				const a = text => console.log(text)
				a('Hi')
				""", """
				Hi
				""");
	}

	@Test
	public void callChain() {
		testInterpreter("""
				const a = () => () => () => console.log('Hi')
				a()()()
				""", """
				Hi
				""");
	}

	@Test
	public void singleGroupedStr() {
		testInterpreter("""
				console.log(('hi'))
				""", """
				hi
				""");
	}

	@Test
	public void singleGroupedNum() {
		testInterpreter("""
				console.log((5.0))
				""", """
				5
				""");
	}

	@Test
	public void selfInvokingFunction() {
		testInterpreter("""
				(() => console.log('im self invoking'))()
				console.log('hi')
				""", """
				im self invoking
				hi
				""");
	}

	@Test
	public void expNumPos() {
		testInterpreter("""
				console.log(1e3)
				""", """
				1000
				""");
	}

	@Test
	public void expNumNeg() {
		testInterpreter("""
				console.log(1e-3)
				""", """
				0.001
				""");
	}

	@Test
	public void redeclarationFuncScope() {
		testInterpreter("""
				let x = 10;
				let func = (a) => { console.log(`${a} / ${x}`); }
				func(5);
				x = 20;
				func(5);
				""", """
				5 / 10
				5 / 20
				""");
	}

	@Test
	public void labelledForBreak() {
		testInterpreter("""
				let i, j;
								
				loop1:
				for (i = 0; i < 3; i++) {
				  loop2:
				  for (j = 0; j < 3; j++) {
				    if (i === 1 && j === 1) {
				      break loop1;
				    }
				    console.log(`i = ${i}, j = ${j}`);
				  }
				}
				""", """
				i = 0, j = 0
				i = 0, j = 1
				i = 0, j = 2
				i = 1, j = 0
				""");
	}

	@Test
	public void labelledBlock() {
		testInterpreter("""
				foo: {
				  console.log('face');
				  break foo;
				  console.log('this will not be executed');
				}
				console.log('swap');
				""", """
				face
				swap
				""");
	}

	@Test
	public void labelledMultiLevelBlock() {
		testInterpreter("""
				a: {
				  console.log('a')
				  
				  b: {
				    c: {
				      break a;
				      console.log('c')
				    }
				    
				    console.log('b')
				  }
				}
								
				console.log('d')
				""", """
				a
				d
				""");
	}

	@Test
	public void weirdClosure() {
		testInterpreter("""
				function closure() {
				    let b = 10;
				    console.log(b); // 10
				    callMe(function() {
				        b++;
				        return b;
				    })
				    console.log(b); // 11
				}
				    
				function callMe(fn) {
				    console.log(fn()); // 11
				}
								
				closure()
				""", """
				10
				11
				11
				""");
	}

	@Test
	public void weirdClosure2() {
		testInterpreter("""
				function giveMeClosure() {
				    let a = 10;
				    return function() {
				      a++;
				      return a;
				    }
				}
								
				console.log(giveMeClosure()())
				""", """
				11
				""");
	}

	// @Test
	public void destructing() {
		testInterpreter("""
				let props = {a: '1', b: '2'}
				let {a, b} = props
				console.log(a)
				console.log(b)
				""", """
				1
				2
				""");
	}
}
