package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Frame;

public record LiteralToken(KeywordToken keyword, Object value) implements StaticToken, Evaluable {
	public static final LiteralToken NULL = new LiteralToken(KeywordToken.NULL, null);
	public static final LiteralToken TRUE = new LiteralToken(KeywordToken.TRUE, Boolean.TRUE);
	public static final LiteralToken FALSE = new LiteralToken(KeywordToken.FALSE, Boolean.FALSE);

	@Override
	public Object eval(Frame frame) {
		return value;
	}

	@Override
	public boolean isPrimary() {
		return true;
	}

	@Override
	public Object getPrimaryValue() {
		return value;
	}

	@Override
	public String toString() {
		return keyword.toString();
	}
}
