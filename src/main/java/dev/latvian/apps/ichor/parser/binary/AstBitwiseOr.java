package dev.latvian.apps.ichor.parser.binary;

public class AstBitwiseOr extends AstBinary {
	@Override
	public void toString(StringBuilder builder) {
		left.toString(builder);
		builder.append('|');
		right.toString(builder);
	}
}
