package dev.latvian.apps.ichor.parser.expression.binary;

public class AstAddSet extends AstModifySet {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("+=");
	}
}
