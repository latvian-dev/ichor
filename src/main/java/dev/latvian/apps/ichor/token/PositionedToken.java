package dev.latvian.apps.ichor.token;

public record PositionedToken(Token token, TokenPos pos) {
	public String asString() {
		return token instanceof NameToken n ? n.name() : token instanceof KeywordToken k ? k.name : token.toString();
	}

	@Override
	public String toString() {
		return "%d:%d %s".formatted(pos.line(), pos.pos(), token);
	}
}
