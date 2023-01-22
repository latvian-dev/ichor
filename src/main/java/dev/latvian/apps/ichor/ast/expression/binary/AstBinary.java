package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstExpression;

public abstract class AstBinary extends AstExpression {
	public Object left;
	public Object right;

	@Override
	public final void append(AstStringBuilder builder) {
		builder.append('(');
		builder.appendValue(left);
		appendSymbol(builder.builder);
		builder.appendValue(right);
		builder.append(')');
	}

	public abstract void appendSymbol(StringBuilder builder);

	@Override
	public Object optimize(Parser parser) {
		left = parser.optimize(left);
		right = parser.optimize(right);
		return this;
	}
}
