package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstNumber extends AstExpression {
	public final double value;
	public final Double valueObj;

	public AstNumber(double v) {
		value = v;
		valueObj = v;
	}

	@Override
	public Object eval(Scope scope) {
		return valueObj;
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