package dev.latvian.apps.ichor.parser.expression;

public class AstCall extends AstExpression {
	public final AstExpression callee;
	public final AstExpression[] arguments;

	public AstCall(AstExpression callee, AstExpression... arguments) {
		this.callee = callee;
		this.arguments = arguments;
	}

	@Override
	public void append(StringBuilder builder) {
		callee.append(builder);
		builder.append('(');

		for (int i = 0; i < arguments.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			arguments[i].append(builder);
		}

		builder.append(')');
	}
}
