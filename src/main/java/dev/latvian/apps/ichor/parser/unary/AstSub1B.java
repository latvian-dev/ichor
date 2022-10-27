package dev.latvian.apps.ichor.parser.unary;

public class AstSub1B extends AstUnary {
	@Override
	public void toString(StringBuilder builder) {
		builder.append("--");
		node.toString(builder);
	}
}
