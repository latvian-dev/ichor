package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;

public record StringToken(String value) implements Token, Evaluable {
	public static final StringToken EMPTY = new StringToken("");

	public static StringToken of(String string) {
		return string.isEmpty() ? EMPTY : new StringToken(string);
	}

	@Override
	public String toString() {
		return '"' + value.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
	}

	@Override
	public Object eval(Scope scope) {
		return value;
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public Object getValue() {
		return value;
	}
}
