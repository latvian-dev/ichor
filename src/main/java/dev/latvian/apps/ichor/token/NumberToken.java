package dev.latvian.apps.ichor.token;

public record NumberToken(double number) implements Token {
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
		return Double.toString(number);
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof NumberToken n && Math.abs(number - n.number) < 0.00001D;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(number);
	}
}
