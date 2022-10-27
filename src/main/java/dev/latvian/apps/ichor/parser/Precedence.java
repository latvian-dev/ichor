package dev.latvian.apps.ichor.parser;

public enum Precedence {
	NONE(true),
	CONDITIONAL(false),
	ASSIGNMENT(false),
	LOGICAL_OR(true),
	LOGICAL_AND(true),
	BITWISE_OR(true),
	BITWISE_XOR(true),
	BITWISE_AND(true),
	EQUALITY(true),
	RELATIONAL(true),
	SHIFT(true),
	ADDITIVE(true),
	MULTIPLICATIVE(true),
	EXPONENT(true),
	UNARY(false),
	POSTFIX(true);

	public final boolean leftToRight;

	Precedence(boolean ltr) {
		leftToRight = ltr;
	}
}
