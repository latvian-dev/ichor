package dev.latvian.apps.ichor.parser.unary;

public class AstSub1E extends AstUnary {
	@Override
	public void toString(StringBuilder builder) {
		node.toString(builder);
		builder.append("--");
	}
}
