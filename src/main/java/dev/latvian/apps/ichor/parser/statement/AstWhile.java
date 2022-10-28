package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.parser.expression.AstExpression;

public class AstWhile extends AstStatement {
	public final AstExpression condition;
	public final AstStatement body;

	public AstWhile(AstExpression condition, AstStatement body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append("while (");
		condition.append(builder);
		builder.append(") ");
		body.append(builder);
	}
}
