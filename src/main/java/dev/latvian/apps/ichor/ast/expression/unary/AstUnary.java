package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.expression.AstExpression;

public abstract class AstUnary extends AstExpression {
	public Object node;

	@Override
	public Object optimize(Parser parser) {
		node = parser.optimize(node);
		return this;
	}
}
