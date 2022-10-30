package dev.latvian.apps.ichor.parser.expression.binary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;
import dev.latvian.apps.ichor.parser.expression.AstExpression;

public abstract class AstBinary extends AstExpression {
	@FunctionalInterface
	public interface Factory {
		Evaluable create(Evaluable left, Evaluable right);
	}

	public Object left;
	public Object right;

	@Override
	public final void append(AstStringBuilder builder) {
		builder.append('(');
		builder.append(left);
		appendSymbol(builder.builder);
		builder.append(right);
		builder.append(')');
	}

	public abstract void appendSymbol(StringBuilder builder);

	public Object evalL(Scope scope) {
		return left instanceof Evaluable eval ? eval.eval(scope) : left;
	}

	public Object evalR(Scope scope) {
		return right instanceof Evaluable eval ? eval.eval(scope) : right;
	}
}
