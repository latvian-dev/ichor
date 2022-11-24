package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.util.Empty;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class InterpreterFileTests {
	public static void testInterpreter(String filename, String match) throws IOException {
		var p = Path.of(filename);

		if (Files.exists(p) && Files.isReadable(p)) {
			var input = String.join("\n", Files.readAllLines(p));
			InterpreterTests.testInterpreter(filename, input, Empty.consumer(), match);
		} else {
			System.out.println("File '" + p + "' not found");
		}
	}

	//@Test
	public void test1() throws IOException {
		testInterpreter("run/test.js", "");
	}

	@Test
	public void testTrimps() throws IOException {
		testInterpreter("run/trimps.js", "");
	}
}
