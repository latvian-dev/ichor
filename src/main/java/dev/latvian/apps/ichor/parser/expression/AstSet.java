package dev.latvian.apps.ichor.parser.expression;

public class AstSet extends AstExpression {
	public final AstExpression from;
	public final String name;
	public final AstExpression value;

	public AstSet(AstExpression from, String name, AstExpression value) {
		this.from = from;
		this.name = name;
		this.value = value;
	}

	@Override
	public void append(StringBuilder builder) {
		from.append(builder);
		builder.append('.');
		builder.append(name);
		builder.append('=');
		value.append(builder);
	}
}
