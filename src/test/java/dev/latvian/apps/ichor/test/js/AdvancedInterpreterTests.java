package dev.latvian.apps.ichor.test.js;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class AdvancedInterpreterTests {
	@Test
	public void callRef1() {
		InterpreterTests.testInterpreter("""
				const l = console.log
				l('Hi')
				""", """
				Hi
				""");
	}

	@Test
	public void callRef2() {
		InterpreterTests.testInterpreter("""
				const c = console
				const l = c.log
				l('Hi')
				""", """
				Hi
				""");
	}

	@Test
	public void callRefArrow() {
		InterpreterTests.testInterpreter("""
				const a = text => console.log(text)
				a('Hi')
				""", """
				Hi
				""");
	}

	@Test
	public void callChain() {
		InterpreterTests.testInterpreter("""
				const a = () => () => () => console.log('Hi')
				a()()()
				""", """
				Hi
				""");
	}

	@Test
	public void specialCalls() {
		InterpreterTests.testInterpreter("""
				let str = "hello"
				console.log(str.toString())
				console.log(str.hashCode())
				console.log(str.hashCode().toString())
				""", """
				hello
				99162322
				99162322
				""");
	}

	@Test
	public void weirdClosure() {
		InterpreterTests.testInterpreter("""
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
		InterpreterTests.testInterpreter("""
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

	@Test
	public void jsClass() {
		InterpreterTests.testInterpreter("""
				class Rect { // AstClass -> ClassPrototype
				  constructor(w, h) {
				    this.w = w
				    this.h = h
				  }
								
				  calcArea() {
				    return w * h
				  }
				  
				  printArea(extra) {
				    console.log(extra + ': ' + calcArea())
				  }
				}
								
				let r = new Rect(30, 50) // ClassPrototype$Instance
				r.printArea('Hi 1')
				""", """
				Hi 1: 1500
				""");
	}

	@Test
	public void jsClassWithParent() {
		InterpreterTests.testInterpreter("""
				let cl = console.log
								
				class TestParent {
				  constructor(param) {
				    this.param = param
				  }
								
				  printTest(y) {
				    cl(this.param + ' x ' + y)
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
				-439 x Hi 1
				9 x Hi 2
				""");
	}

	@Test
	public void ifaceRunnableArrow() {
		InterpreterTests.testInterpreter("""
				console.log('a')
				Advanced.runnable(() => {
				  console.log('b')
				  console.log('c')
				})
				""", """
				a
				b
				c
				""");
	}

	@Test
	public void ifaceRunnableRef() {
		InterpreterTests.testInterpreter("""
				console.log('a')
								
				let func = () => {
				  console.log('b')
				  console.log('c')
				}
								
				Advanced.runnable(func)
				""", """
				a
				b
				c
				""");
	}

	@Test
	public void ifaceConsumerArrow() {
		InterpreterTests.testInterpreter("""
				Advanced.consumer(9, txt => console.log(txt))
				""", """
				9 x hello
				""");
	}

	@Test
	public void ifaceConsumerRef() {
		InterpreterTests.testInterpreter("""
				Advanced.consumer(9, console.log)
				""", """
				9 x hello
				""");
	}

	@Test
	public void ifaceSupplierArrow() {
		InterpreterTests.testInterpreter("""
				console.log(Advanced.supplier(() => 4.4))
				""", """
				4.4
				""");
	}

	@Test
	public void numberTypeCasting() {
		InterpreterTests.testInterpreter("""
				Advanced.testFloat(5.3, console)
				Advanced.testFloat(Advanced.short1, console)
				Advanced.testFloatSupplier(() => Advanced.short2, console)
				""", """
				Float value: 5.3
				Float value: 30
				Float value: 40
				""");
	}

	@Test
	public void javaMap() {
		InterpreterTests.testInterpreter("""
				Advanced.testMap({a: 4, b: 4.5, c: ['a', 'b', 'c']}, console)
				""", """
				Map: {a=4.0, b=4.5, c=[a, b, c]}
				""");
	}
}
