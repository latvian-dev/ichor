package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.expression.AstBoolean;

public class BooleanToken implements Token {
	public static final BooleanToken TRUE = new BooleanToken(true);
	public static final BooleanToken FALSE = new BooleanToken(false);

	public final boolean value;

	private BooleanToken(boolean v) {
		value = v;
	}

	@Override
	public String toString() {
		return value ? "true" : "false";
	}

	@Override
	public Evaluable toEvaluable(Parser parser, TokenPos pos) {
		return new AstBoolean(value).pos(pos);
	}
}
