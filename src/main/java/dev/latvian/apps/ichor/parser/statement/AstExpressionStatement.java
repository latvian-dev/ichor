package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.parser.expression.AstExpression;

public class AstExpressionStatement extends AstStatement {
	public final AstExpression expression;

	public AstExpressionStatement(AstExpression expression) {
		this.expression = expression;
	}

	@Override
	public void append(StringBuilder builder) {
		expression.append(builder);
		builder.append(';');
	}
}
