package dev.latvian.apps.ichor.parser.expression.binary;

public class AstBitwiseAndSet extends AstModifySet {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("&=");
	}
}
