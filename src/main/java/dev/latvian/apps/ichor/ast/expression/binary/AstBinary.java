package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Evaluable;
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

	@FunctionalInterface
	public interface Factory {
		Evaluable create(Evaluable left, Evaluable right);
	}
}
