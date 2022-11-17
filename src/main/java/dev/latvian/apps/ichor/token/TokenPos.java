package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.TokenSource;

public record TokenPos(TokenSource source, int line, int pos) implements TokenPosSupplier {
	public static final TokenPos UNKNOWN = new TokenPos(null, -1, -1);

	@Override
	public String toString() {
		return this == UNKNOWN ? "<unknown>" : "%s:%d:%d".formatted(source, line + 1, pos + 1);
	}

	@Override
	public TokenPos getPos() {
		return this;
	}
}
