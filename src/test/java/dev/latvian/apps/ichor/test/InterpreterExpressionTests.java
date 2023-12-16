package dev.latvian.apps.ichor.test;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;

@Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class InterpreterExpressionTests {
	private static void test(String eval, String match) {
		InterpreterTests.testInterpreter("console.log(" + eval + ")", match);
	}

	@Test
	public void add() {
		test("2 + 3", "5");
	}

	@Test
	public void addf() {
		test("2.5 + 3.8", "6.3");
	}

	@Test
	public void adds() {
		test("'h' + 'i'", "hi");
	}

	@Test
	public void addns() {
		test("'11' + 1", "111");
	}

	@Test
	public void subns() {
		test("'11' - 1", "10");
	}

	@Test
	public void mul() {
		test("2 * 3", "6");
	}

	@Test
	public void mulf() {
		test("2.5 * 3.0", "7.5");
	}

	@Test
	public void div() {
		test("3 / 2", "1.5");
	}

	@Test
	public void divf() {
		test("2 / 0.5", "4");
	}

	@Test
	public void eq() {
		test("3 == 3.0", "true");
	}

	@Test
	public void seq() {
		test("3 === 3.0", "true");
	}

	@Test
	public void neq() {
		test("3 != 30.0", "true");
	}

	@Test
	public void sneq() {
		test("3 !== 3.0", "false");
	}

	@Test
	public void lt() {
		test("1 < 3.0", "true");
	}

	@Test
	public void lte() {
		test("1 <= 1.0", "true");
	}

	@Test
	public void gt() {
		test("3 > 1.0", "true");
	}

	@Test
	public void gte() {
		test("3 >= 3.0", "true");
	}
}
