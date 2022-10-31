package dev.latvian.apps.ichor.token;

public record TokenPos(int line, int pos) {
	public static final TokenPos UNKNOWN = new TokenPos(-1, -1);

	@Override
	public String toString() {
		return line == -1 || pos == -1 ? "unknown" : (line + ":" + pos);
	}
}
