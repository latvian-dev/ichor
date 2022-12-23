package dev.latvian.apps.ichor.test.js;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;

@Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class InterpreterOperatorTests {
	private static void testOp(String eval, String match) {
		InterpreterTests.testInterpreter("print(" + eval + ")", match);
	}

	@Test
	public void add() {
		testOp("2 + 3", "5");
	}

	@Test
	public void addf() {
		testOp("2.5 + 3.8", "6.3");
	}

	@Test
	public void adds() {
		testOp("'h' + 'i'", "hi");
	}

	@Test
	public void addns() {
		testOp("'11' + 1", "111");
	}

	@Test
	public void subns() {
		testOp("'11' - 1", "10");
	}

	@Test
	public void mul() {
		testOp("2 * 3", "6");
	}

	@Test
	public void mulf() {
		testOp("2.5 * 3.0", "7.5");
	}

	@Test
	public void div() {
		testOp("3 / 2", "1.5");
	}

	@Test
	public void divf() {
		testOp("2 / 0.5", "4");
	}
}
