package dev.latvian.apps.ichor.parser.expression;

public class AstLiteral extends AstExpression {
	public final Object value;

	public AstLiteral(Object value) {
		this.value = value;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append(value);
	}
}
