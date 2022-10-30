package dev.latvian.apps.ichor.parser.expression.binary;

public class AstModSet extends AstModifySet {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("%=");
	}
}
