package dev.latvian.apps.ichor.parser.expression;

public class AstGrouping extends AstExpression {
	public final AstExpression expression;

	public AstGrouping(AstExpression expression) {
		this.expression = expression;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append('(');
		expression.append(builder);
		builder.append(')');
	}
}
