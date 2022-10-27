package dev.latvian.apps.ichor.parser.binary;

public class AstMod extends AstBinary {
	@Override
	public void toString(StringBuilder builder) {
		left.toString(builder);
		builder.append('%');
		right.toString(builder);
	}
}
