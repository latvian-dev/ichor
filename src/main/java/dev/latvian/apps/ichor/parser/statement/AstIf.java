package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.parser.expression.AstExpression;

public class AstIf extends AstStatement {
	public final AstExpression condition;
	public final AstStatement trueBody;
	public final AstStatement falseBody;

	public AstIf(AstExpression condition, AstStatement ifTrue, AstStatement ifFalse) {
		this.condition = condition;
		this.trueBody = ifTrue;
		this.falseBody = ifFalse;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append("if (");
		condition.append(builder);
		builder.append(") ");
		trueBody.append(builder);

		if (falseBody != null) {
			builder.append(" else ");
			falseBody.append(builder);
		}
	}
}
