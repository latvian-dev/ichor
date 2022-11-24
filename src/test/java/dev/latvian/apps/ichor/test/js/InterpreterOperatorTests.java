package dev.latvian.apps.ichor.test.js;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;

@Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class InterpreterOperatorTests {
	@Test
	public void add() {
		InterpreterTests.testInterpreter("print(2 + 3)", "5.0");
	}

	@Test
	public void addf() {
		InterpreterTests.testInterpreter("print(2.5 + 3.8)", "6.3");
	}

	@Test
	public void adds() {
		InterpreterTests.testInterpreter("print('h' + 'i')", "hi");
	}

	@Test
	public void addns() {
		InterpreterTests.testInterpreter("print('11' + 1)", "111.0");
	}

	@Test
	public void subns() {
		InterpreterTests.testInterpreter("print('11' - 1)", "10.0");
	}

	@Test
	public void mul() {
		InterpreterTests.testInterpreter("print(2 * 3)", "6.0");
	}

	@Test
	public void mulf() {
		InterpreterTests.testInterpreter("print(2.5 * 3.0)", "7.5");
	}

	@Test
	public void div() {
		InterpreterTests.testInterpreter("print(3 / 2)", "1.5");
	}

	@Test
	public void divf() {
		InterpreterTests.testInterpreter("print(2 / 0.5)", "4.0");
	}
}
