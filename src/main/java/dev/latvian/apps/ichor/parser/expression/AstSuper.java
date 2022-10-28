package dev.latvian.apps.ichor.parser.expression;

public class AstSuper extends AstExpression {
	public final String target;

	public AstSuper(String target) {
		this.target = target;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append("super.");
		builder.append(target);
	}
}
