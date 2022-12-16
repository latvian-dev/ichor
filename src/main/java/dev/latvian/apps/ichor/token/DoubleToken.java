package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.expression.AstDouble;

public record DoubleToken(double value) implements Token {
	public static final DoubleToken ZERO = new DoubleToken(0.0);
	public static final DoubleToken ONE = new DoubleToken(1.0);

	public static DoubleToken of(double num) {
		if (num == 0.0) {
			return ZERO;
		} else if (num == 1.0) {
			return ONE;
		} else {
			return new DoubleToken(num);
		}
	}

	@Override
	public String toString() {
		return Double.toString(value);
	}

	@Override
	public Evaluable toEvaluable(Parser parser, TokenPos pos) {
		return new AstDouble(value).pos(pos);
	}
}
