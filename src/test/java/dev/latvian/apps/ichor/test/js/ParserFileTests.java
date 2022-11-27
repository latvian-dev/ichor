package dev.latvian.apps.ichor.test.js;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ParserFileTests {
	public static void testParser(String filename) throws IOException {
		var p = Path.of(filename);

		if (Files.exists(p) && Files.isReadable(p)) {
			var input = String.join("\n", Files.readAllLines(p));
			ParserTests.testParserAst(filename, input, "*");
		} else {
			System.out.println("File '" + p + "' not found");
		}
	}

	@Test
	public void test1() throws IOException {
		testParser("run/test.js");
	}

	@Test
	public void trimps() throws IOException {
		testParser("run/trimps.js");
	}
}
