package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.expression.AstExpression;

public abstract class AstUnary extends AstExpression {
	public Evaluable node;

	@Override
	public Evaluable optimize(Parser parser) {
		node = node.optimize(parser);
		return this;
	}
}
