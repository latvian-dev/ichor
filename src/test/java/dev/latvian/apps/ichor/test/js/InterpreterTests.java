package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.RootScope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ScopeExit;
import dev.latvian.apps.ichor.lang.js.ContextJS;
import dev.latvian.apps.ichor.lang.js.ParserJS;
import dev.latvian.apps.ichor.lang.js.TokenStreamJS;
import dev.latvian.apps.ichor.test.AdvancedTestUtils;
import dev.latvian.apps.ichor.test.ReflectionExample;
import dev.latvian.apps.ichor.test.TestConsole;
import dev.latvian.apps.ichor.util.Empty;
import dev.latvian.apps.ichor.util.IchorUtils;
import dev.latvian.apps.ichor.util.NamedTokenSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;
import java.util.function.Consumer;

// @Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class InterpreterTests {
	public static ContextJS context;

	static {
		context = new ContextJS();
		context.setInterpretingTimeout(1500L);
		context.setTokenStreamTimeout(1500L);
	}

	public static void testInterpreter(String filename, String input, Consumer<RootScope> rootScopeCallback, String match) {
		var matchStr = Arrays.asList(match.split("\n"));
		System.out.println("--- Interpreter Test ---");
		System.out.println();
		System.out.println("Input:");
		IchorUtils.printLines(Arrays.asList(input.split("\n")));
		System.out.println();
		System.out.println("Expected:");
		IchorUtils.printLines(matchStr);
		System.out.println();

		var rootScope = new RootScope(context);
		rootScope.addSafePrototypes();
		var console = new TestConsole(System.out);
		rootScope.addImmutable("console", console);
		rootScope.addImmutable("Advanced", new AdvancedTestUtils(console));
		rootScopeCallback.accept(rootScope);

		var tokenStream = new TokenStreamJS(context, new NamedTokenSource(filename), input);
		var rootToken = tokenStream.getRootToken();
		var parser = new ParserJS(context, rootScope, rootToken);
		var ast = parser.parse();
		var astStr = ast.toString();

		System.out.println("Parsed:");
		System.out.println(astStr);
		System.out.println();

		try {
			ast.interpretSafe(context, rootScope);
		} catch (ScopeExit ex) {
			throw ex;
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		System.out.println();
		System.out.println("Returned:");
		IchorUtils.printLines(console.output);
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
				""", AstStringBuilder.wrapNumber(IchorUtils.MAX_SAFE_INTEGER));
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

	@Test
	public void propertyTest() {
		testInterpreter("""
				console.log('A')
				console.log('B')
				console.log('C')
				console.log(console.lastLine)
				console.log(console.testBean === 30)
				""", """
				A
				B
				C
				C
				true
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

	@Test
	public void prototypeProperty() {
		testInterpreter("""
				console.log("".__proto__)
				console.log(String.prototype)
				console.log(6.__proto__)
				""", """
				[JavaClass java.lang.String]
				[JavaClass java.lang.String]
				[JavaClass java.lang.Number]
				""");
	}

	@Test
	public void templateLiteralTag() {
		testInterpreter("""
				console.log(String.raw`Hi`)
				""", """
				Hi
				""");
	}

	@Test
	public void arrPush() {
		testInterpreter("""
				const arr = ['a', 'b', 'c']
				arr.push('d')
				console.log(arr)
				""", """
				['a', 'b', 'c', 'd']
				""");
	}

	@Test
	public void arrPop() {
		testInterpreter("""
				const arr = ['a', 'b', 'c']
				const last = arr.pop()
				console.log(arr)
				console.log(last)
				""", """
				['a', 'b']
				c
				""");
	}

	@Test
	public void arrUnshift() {
		testInterpreter("""
				const arr = ['a', 'b', 'c']
				arr.unshift('d')
				console.log(arr)
				""", """
				['d', 'a', 'b', 'c']
				""");
	}

	@Test
	public void arrShift() {
		testInterpreter("""
				const arr = ['a', 'b', 'c']
				const first = arr.shift()
				console.log(arr)
				console.log(first)
				""", """
				['b', 'c']
				a
				""");
	}

	@Test
	public void declareNormal() {
		testInterpreter("""
				const obj = {a: 1, b: {c: 2}, d: ['a', 'b', 'c']}
				console.log(obj.a)
				console.log(obj.b.c)
				""", """
				1
				2
				""");
	}

	@Test
	public void declareDestructObj() {
		testInterpreter("""
				const obj = {a: 1, b: {c: 2}, d: ['a', 'b', 'c'], e: 'f', g: 'h'}
				const { a = 5, b, d: x, ...rest } = obj
				console.log(a)
				console.log(x[1])
				console.log(b.c)
				console.log(rest)
				""", """
				1
				b
				2
				{e: 'f', g: 'h'}
				""");
	}

	@Test
	public void declareDestructArr() {
		testInterpreter("""
				const obj = {a: 1, b: {c: 2}, d: ['a', 'b', 'c']}
				const [,x1, ...rest] = obj.d
				console.log(x1)
				console.log(rest)
				""", """
				b
				['c']
				""");
	}

	@Test
	public void declareDestructObjNested() {
		testInterpreter("""
				const obj = {a: 1, b: {c: 2}, d: ['a', 'b', 'c']}
				const { b: { c: x }} = obj
				console.log(x)
				""", """
				2
				""");
	}

	@Test
	public void z_last() {
		System.out.println("Hi");
	}
}
