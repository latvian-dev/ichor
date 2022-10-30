package dev.latvian.apps.ichor.parser.expression.binary;

public class AstSubSet extends AstModifySet {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("-=");
	}
}
