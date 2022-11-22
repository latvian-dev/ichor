package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.ast.expression.AstExpression;

public abstract class AstUnary extends AstExpression {
	public Evaluable node;

	@Override
	public Evaluable optimize() {
		node = node.optimize();
		return this;
	}
}
