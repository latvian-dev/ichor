package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Frame;

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
	public Object eval(Frame frame) {
		return value;
	}

	@Override
	public boolean isPrimary() {
		return true;
	}

	@Override
	public Object getPrimaryValue() {
		return value;
	}
}
