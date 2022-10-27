package dev.latvian.apps.ichor.parser.unary;

public class AstBitwiseNot extends AstUnary {
	@Override
	public void toString(StringBuilder builder) {
		builder.append('~');
		node.toString(builder);
	}
}
