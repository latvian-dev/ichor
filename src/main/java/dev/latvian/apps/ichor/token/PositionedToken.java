package dev.latvian.apps.ichor.token;

public record PositionedToken(Token token, int line, int pos) {
	public String asString() {
		return token instanceof NameToken n ? n.name() : token.getValue().toString();
	}

	@Override
	public String toString() {
		return line + ":" + pos + " " + token;
	}
}
