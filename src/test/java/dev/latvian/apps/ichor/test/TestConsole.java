package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.List;

public record TestConsole(PrintStream printStream, List<String> output) implements Prototype {
	@Override
	public String getPrototypeName() {
		return "console";
	}

	@Override
	public Object call(Scope scope, Object[] args, @Nullable Object self) {

		for (Object o : args) {
			var s = scope.getContext().asString(scope, o);

			if (!s.isBlank()) {
				output.add(s.trim());
			}

			printStream.println("> " + s);
		}

		return null;
	}

	@Override
	public String toString() {
		return "console";
	}
}
