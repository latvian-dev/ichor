package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.parser.expression.AstExpression;

public class AstConstStatement extends AstStatement {
	public final String name;
	public final AstExpression initializer;

	public AstConstStatement(String name, AstExpression initializer) {
		this.name = name;
		this.initializer = initializer;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append(name);

		if (initializer != null) {
			builder.append('=');
			initializer.append(builder);
		}

		builder.append(';');
	}
}
