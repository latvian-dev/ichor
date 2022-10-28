package dev.latvian.apps.ichor.parser.expression.unary;

import dev.latvian.apps.ichor.parser.Ast;
import dev.latvian.apps.ichor.parser.expression.AstExpression;

public abstract class AstUnary extends AstExpression {
	@FunctionalInterface
	public interface Factory {
		AstUnary create(Ast node);
	}

	public final Ast node;

	public AstUnary(Ast node) {
		this.node = node;
	}
}
