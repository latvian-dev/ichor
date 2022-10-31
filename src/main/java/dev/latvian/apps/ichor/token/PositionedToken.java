package dev.latvian.apps.ichor.token;

public record PositionedToken(Token token, TokenPos pos) {
	public String asString() {
		return token instanceof NameToken n ? n.name() : token.getValue().toString();
	}

	@Override
	public String toString() {
		return "%d:%d %s".formatted(pos.line(), pos.pos(), token);
	}
}
