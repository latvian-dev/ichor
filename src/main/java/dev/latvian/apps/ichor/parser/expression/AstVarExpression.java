package dev.latvian.apps.ichor.parser.expression;

public class AstVarExpression extends AstExpression {
	public final String name;

	public AstVarExpression(String name) {
		this.name = name;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append(name);
	}
}
