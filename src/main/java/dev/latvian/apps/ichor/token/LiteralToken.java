package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;

public record LiteralToken(KeywordToken keyword, Object value) implements StaticToken, Evaluable {
	public static final LiteralToken NULL = new LiteralToken(KeywordToken.NULL, null);
	public static final LiteralToken TRUE = new LiteralToken(KeywordToken.TRUE, Boolean.TRUE);
	public static final LiteralToken FALSE = new LiteralToken(KeywordToken.FALSE, Boolean.FALSE);

	@Override
	public Object eval(Scope scope) {
		return value;
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return keyword.name;
	}
}
