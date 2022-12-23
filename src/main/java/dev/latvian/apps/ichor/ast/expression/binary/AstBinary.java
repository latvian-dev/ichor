package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstExpression;
import dev.latvian.apps.ichor.token.BooleanToken;
import dev.latvian.apps.ichor.token.DoubleToken;

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
	public Evaluable optimize(Parser parser) {
		left = left.optimize(parser);
		right = right.optimize(parser);

		if (left instanceof DoubleToken && right instanceof DoubleToken) {
			return DoubleToken.of(evalDouble(null));
		} else if (left instanceof BooleanToken && right instanceof BooleanToken) {
			return BooleanToken.of(evalBoolean(null));
		}

		return this;
	}
}
