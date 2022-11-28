package dev.latvian.apps.ichor.token;

public record TokenPos(TokenSource source, int row, int col) implements TokenPosSupplier {
	public static final TokenPos UNKNOWN = new TokenPos(null, 0, 0);

	@Override
	public String toString() {
		if (this == UNKNOWN) {
			return "?:?";
		} else {
			var s = source == null ? null : source.getSourceName();

			if (s == null || s.isEmpty()) {
				return row + ":" + col;
			} else {
				return s + ":" + row + ":" + col;
			}
		}
	}

	@Override
	public TokenPos getPos() {
		return this;
	}
}
