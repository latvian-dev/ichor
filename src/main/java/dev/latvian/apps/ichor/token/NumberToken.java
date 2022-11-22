package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.expression.AstNumber;

public record NumberToken(double value) implements Token {
	public static final NumberToken ZERO = new NumberToken(0.0);
	public static final NumberToken ONE = new NumberToken(1.0);

	public static NumberToken of(double num) {
		if (num == 0.0) {
			return ZERO;
		} else if (num == 1.0) {
			return ONE;
		} else {
			return new NumberToken(num);
		}
	}

	@Override
	public String toString() {
		return Double.toString(value);
	}

	@Override
	public Evaluable toEvaluable(Parser parser, TokenPos pos) {
		return new AstNumber(value).pos(pos);
	}
}
