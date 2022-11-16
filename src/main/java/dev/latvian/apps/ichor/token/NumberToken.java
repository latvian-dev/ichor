package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstAppendable;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public record NumberToken(double value) implements Token, Evaluable, AstAppendable {
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
	public boolean equals(Object o) {
		return o == this || o instanceof NumberToken n && Math.abs(value - n.value) < 0.00001D;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(value);
	}

	@Override
	public Object eval(Scope scope) {
		return value;
	}

	@Override
	public double evalDouble(Scope scope) {
		return value;
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return value != 0D;
	}

	@Override
	public Evaluable toEvaluable() {
		return this;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.builder.append(value);
	}
}
