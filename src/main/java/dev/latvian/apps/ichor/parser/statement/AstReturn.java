package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.parser.expression.AstExpression;

public class AstReturn extends AstStatement {
	public final AstExpression value;

	public AstReturn(AstExpression value) {
		this.value = value;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append("return ");
		value.append(builder);
		builder.append(';');
	}
}
