package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstBoolean;
import dev.latvian.apps.ichor.ast.expression.AstExpression;
import dev.latvian.apps.ichor.ast.expression.AstNumber;

public abstract class AstBinary extends AstExpression {
	public Evaluable left;
	public Evaluable right;

	@Override
	public final void append(AstStringBuilder builder) {
		builder.append('(');
		builder.append(left);
		appendSymbol(builder.builder);
		builder.append(right);
		builder.append(')');
	}

	public abstract void appendSymbol(StringBuilder builder);

	@Override
	public Evaluable optimize() {
		left = left.optimize();
		right = right.optimize();

		if (left instanceof AstNumber && right instanceof AstNumber) {
			return new AstNumber(evalDouble(null)).pos(((AstNumber) left).pos);
		} else if (left instanceof AstBoolean && right instanceof AstBoolean) {
			return new AstBoolean(evalBoolean(null)).pos(((AstBoolean) left).pos);
		}

		return this;
	}
}
