package dev.latvian.apps.ichor.parser.expression.binary;

public class AstBitwiseOrSet extends AstModifySet {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("|=");
	}
}
