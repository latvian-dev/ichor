package dev.latvian.apps.ichor.token;

public record StringToken(String string) implements Token {
	public static final StringToken EMPTY = new StringToken("");

	public static StringToken of(String string) {
		return string.isEmpty() ? EMPTY : new StringToken(string);
	}

	@Override
	public String toString() {
		return '"' + string.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
	}
}
