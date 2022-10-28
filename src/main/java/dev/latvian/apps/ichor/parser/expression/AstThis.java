package dev.latvian.apps.ichor.parser.expression;

public class AstThis extends AstExpression {
	@Override
	public void append(StringBuilder builder) {
		builder.append("this");
	}
}
