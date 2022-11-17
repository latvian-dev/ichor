package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstIn extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append(" in ");
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		//TODO: Implement
		return false;
	}
}
