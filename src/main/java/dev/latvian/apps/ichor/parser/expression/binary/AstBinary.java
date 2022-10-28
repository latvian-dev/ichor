package dev.latvian.apps.ichor.parser.expression.binary;

import dev.latvian.apps.ichor.parser.expression.AstExpression;

public abstract class AstBinary extends AstExpression {
	@FunctionalInterface
	public interface Factory {
		AstBinary create(AstExpression left, AstExpression right);
	}

	public final AstExpression left;
	public final AstExpression right;

	public AstBinary(AstExpression left, AstExpression right) {
		this.left = left;
		this.right = right;
		this.line = left.line;
		this.pos = left.pos;
	}

	@Override
	public final void append(StringBuilder builder) {
		builder.append('(');
		left.append(builder);
		appendSymbol(builder);
		right.append(builder);
		builder.append(')');
	}

	public abstract void appendSymbol(StringBuilder builder);
}
