package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstAppendable;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class DoubleToken implements Token, Evaluable, AstAppendable {
	public static final DoubleToken ZERO = new DoubleToken(0.0);
	public static final DoubleToken ONE = new DoubleToken(1.0);

	public final double value;
	public final Double valueObj;

	public static DoubleToken of(double num) {
		if (num == 0.0) {
			return ZERO;
		} else if (num == 1.0) {
			return ONE;
		} else {
			return new DoubleToken(num);
		}
	}

	private DoubleToken(double num) {
		value = num;
		valueObj = num;
	}

	@Override
	public String toString() {
		var builder = new StringBuilder();
		AstStringBuilder.wrapNumber(value, builder);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Double.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof DoubleToken t && t.value == value;
	}

	@Override
	public Evaluable toEvaluable(Parser parser, TokenPos pos) {
		return this;
	}

	@Override
	public Object eval(Scope scope) {
		return valueObj;
	}

	@Override
	public void evalString(Scope scope, StringBuilder builder) {
		AstStringBuilder.wrapNumber(valueObj, builder);
	}

	@Override
	public double evalDouble(Scope scope) {
		return value;
	}

	@Override
	public int evalInt(Scope scope) {
		return (int) value;
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return value != 0D;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.builder.append(value);
	}

	@Override
	public boolean equals(Object right, Scope scope, boolean shallow) {
		return Math.abs(value - (right instanceof Number n ? n.doubleValue() : Double.NaN)) < 0.00001D;
	}
}
