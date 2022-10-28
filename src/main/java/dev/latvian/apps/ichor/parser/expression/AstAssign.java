package dev.latvian.apps.ichor.parser.expression;

public class AstAssign extends AstExpression {
	public final String name;
	public final AstExpression value;

	public AstAssign(String name, AstExpression value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append(name);
		builder.append('=');
		value.append(builder);
	}
}
