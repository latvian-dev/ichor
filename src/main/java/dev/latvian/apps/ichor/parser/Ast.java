package dev.latvian.apps.ichor.parser;

import dev.latvian.apps.ichor.token.PositionedToken;

public abstract class Ast {
	public static final Ast[] EMPTY_ARRAY = new Ast[0];

	public int line = -1;
	public int pos = -1;

	@Override
	public String toString() {
		var sb = new StringBuilder();
		append(sb);
		return sb.toString();
	}

	public Ast pos(PositionedToken token) {
		line = token.line();
		pos = token.pos();
		return this;
	}

	public Ast pos(Ast other) {
		line = other.line;
		pos = other.pos;
		return this;
	}

	public abstract void append(StringBuilder builder);
}
