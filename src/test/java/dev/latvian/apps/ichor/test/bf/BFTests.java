package dev.latvian.apps.ichor.test.bf;

import dev.latvian.apps.ichor.lang.bf.ContextBF;
import dev.latvian.apps.ichor.lang.bf.ParserBF;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class BFTests {
	public static ContextBF context;

	static {
		context = new ContextBF();
		context.setInterpretingTimeout(1500L);
		context.setTokenStreamTimeout(1500L);
		context.setMaxMemory(8192);
	}

	public static void testInterpreter(String code, String match) {
		System.out.println("--- Interpreter Test ---");
		System.out.println();
		System.out.println("Input:");
		System.out.println(code);
		System.out.println();
		System.out.println("Expected:");
		System.out.println(match);
		System.out.println();

		var parser = new ParserBF(context, code);
		var output = parser.expression();

		System.out.println();
		System.out.println("Returned:");
		System.out.println(output);
		Assertions.assertEquals(match, output);
	}

	@Test
	public void helloWorld() {
		testInterpreter("++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++..+++.>>.<-.<.+++.------.--------.>>+.", "Hello World!");
	}
}
