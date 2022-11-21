package dev.latvian.apps.ichor.token;

public record TokenPos(TokenSource source, int row, int col) implements TokenPosSupplier {
	public static final TokenPos UNKNOWN = new TokenPos(null, 0, 0);

	@Override
	public String toString() {
		return this == UNKNOWN ? "<unknown>" : "%s:%d:%d".formatted(source, row, col);
	}

	@Override
	public TokenPos getPos() {
		return this;
	}
}
