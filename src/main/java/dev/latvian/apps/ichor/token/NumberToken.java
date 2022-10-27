package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Frame;

public record NumberToken(double value) implements Token, Evaluable {
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
}
