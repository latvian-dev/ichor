package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.RootScope;
import dev.latvian.apps.ichor.ast.AppendableAst;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.lang.js.ContextJS;
import dev.latvian.apps.ichor.lang.js.ParserJS;
import dev.latvian.apps.ichor.lang.js.TokenStreamJS;
import dev.latvian.apps.ichor.util.IchorUtils;
import dev.latvian.apps.ichor.util.NamedTokenSource;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ParserTests {
	public static void testParserAst(String filename, String input, String match) {
		System.out.println("--- Parser Test ---");

		if (match.equals("*")) {
			System.out.println("Input: <" + filename + ">");
		} else {
			IchorUtils.printLines(Arrays.asList(input.split("\n")));
		}

		System.out.println("Expected: " + match);
		var cx = new ContextJS();
		var tokenStream = new TokenStreamJS(cx, new NamedTokenSource(filename), input);
		var rootToken = tokenStream.getRootToken();
		var parser = new ParserJS(cx, new RootScope(cx), rootToken);
		var ast = parser.parse();

		var sb = new AstStringBuilder();
		((AppendableAst) ast).append(sb);

		if (!match.equals("*") && !match.equals("**")) {
			var astStr = sb.toString();
			System.out.println("Parsed:   " + astStr);
			Assertions.assertEquals(match, astStr);
		}
	}

	public static void testParserAst(String input, String match) {
		testParserAst("", input, match);
	}

	@TestFactory
	@SuppressWarnings("resource")
	@Ignore
	public Stream<DynamicTest> fileTests() throws IOException {
		var root = Path.of("parser_file_tests");

		return Files.exists(root) && Files.isDirectory(root) ? Files.walk(root)
				.filter(p -> Files.isRegularFile(p) && Files.isReadable(p) && p.toString().endsWith(".js"))
				.map(p -> {
					var name = root.relativize(p).toString().replace('\\', '/');

					return DynamicTest.dynamicTest(name, () -> {
						var input = String.join("\n", Files.readAllLines(p));
						testParserAst(name, input, "*");
					});
				}) : Stream.empty();
	}

	@TestFactory
	@SuppressWarnings("resource")
	public Stream<DynamicTest> test262Tests() throws IOException {
		var root = Path.of("test262/pass");

		return Files.exists(root) && Files.isDirectory(root) ? Files.walk(root)
				.filter(p -> Files.isRegularFile(p) && Files.isReadable(p) && p.toString().endsWith(".js"))
				.map(p -> {
					var name = root.relativize(p).toString().replace('\\', '/');

					return DynamicTest.dynamicTest(name, () -> {
						var input = String.join("\n", Files.readAllLines(p));
						testParserAst(name, input, "**");
					});
				}) : Stream.empty();
	}

	@Test
	public void temp262() {
		testParserAst("a: for (;;) break a", "**");
	}

	@Test
	public void number() {
		testParserAst("const x = 4.0;", "const x=4.0;");
	}

	@Test
	public void confusingArg() {
		testParserAst("""
				let a = {b: 1, c: true}
				let d = true
				print(a.b = (a.c) ? d : false)
				""", "let a={b=1, c=true};let d=true;print(a.b=(a.c?d:false));");
	}

	@Test
	public void numberTypes() {
		testParserAst("""
				let numbers = [
					304,
					592.40,
					-4992.0042,
					0b1010,
					0o777,
					0x1234,
					0e-5,
					0x1234.5678p10,
					0x1234.5678p-10,
					0x1234.5678P10,
				]
				""", "**");
	}
}
