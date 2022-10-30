package dev.latvian.apps.ichor.parser.expression.binary;

public class AstRshSet extends AstModifySet {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append(">>=");
	}
}
