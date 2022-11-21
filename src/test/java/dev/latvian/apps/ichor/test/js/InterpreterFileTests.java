package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.util.EmptyArrays;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// @Timeout(value = 3, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class InterpreterFileTests {
	public static void testInterpreter(String filename, String match) throws IOException {
		var p = Path.of(filename);

		if (Files.exists(p) && Files.isReadable(p)) {
			var input = String.join("\n", Files.readAllLines(p));
			InterpreterTests.testInterpreter(filename, input, EmptyArrays.consumer(), match);
		} else {
			System.out.println("File '" + p + "' not found");
		}
	}

	@Test
	public void test1() throws IOException {
		testInterpreter("run/test.js", "256.0");
	}
}
