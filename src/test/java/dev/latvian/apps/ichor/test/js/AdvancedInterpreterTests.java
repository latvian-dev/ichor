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
				class Test { // AstClass -> ClassPrototype
				  constructor(param) {
				    this.param = param
				  }
								
				  printTest(y) {
				    console.log(this.param + ' x ' + y)
				  }
				}
								
				let t1 = new Test(-439) // ClassPrototype$Instance
				t1.printTest('Hi 1')
				""", """
				-439 x Hi 1
				""");
	}

	@Test
	public void jsClassWithParent() {
		InterpreterTests.testInterpreter("""
				class TestParent {
				  constructor(param) {
				    this.param = param
				  }
								
				  printTest(y) {
				    console.log(this.param + ' x ' + y)
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
	public void ifaceRunnable() {
		InterpreterTests.testInterpreter("""
				console.log('a')
				IFaces.runnable(() => {
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
	public void ifaceSupplier() {
		InterpreterTests.testInterpreter("""
				IFaces.consumer(9, txt => console.log(txt))
				""", """
				9 x hello
				""");
	}

	@Test
	public void ifaceSupplier2() {
		InterpreterTests.testInterpreter("""
				IFaces.consumer(9, console.log)
				""", """
				9 x hello
				""");
	}
}
