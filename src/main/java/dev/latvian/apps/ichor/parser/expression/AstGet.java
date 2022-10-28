package dev.latvian.apps.ichor.parser.expression;

public class AstGet extends AstExpression {
	public final AstExpression from;
	public final String name;

	public AstGet(AstExpression from, String name) {
		this.from = from;
		this.name = name;
	}

	@Override
	public void append(StringBuilder builder) {
		from.append(builder);
		builder.append('.');
		builder.append(name);
	}
}
