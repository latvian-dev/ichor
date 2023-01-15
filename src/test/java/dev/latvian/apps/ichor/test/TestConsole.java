package dev.latvian.apps.ichor.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public final class TestConsole {
	private final PrintStream printStream;
	public final List<String> output;
	public String lastLine = null;

	public TestConsole(PrintStream printStream) {
		this.printStream = printStream;
		this.output = new ArrayList<>();
	}

	public void log(String s) {
		if (s == null) {
			s = "";
		}

		lastLine = s;

		if (!s.isBlank()) {
			output.add(s.trim());
		}

		printStream.println("> " + s);
	}

	@Override
	public String toString() {
		return "TestConsoleImpl";
	}
}
