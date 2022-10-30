package dev.latvian.apps.ichor.parser.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstLt extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('<');
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return scope.getContext().asDouble(scope, left) < scope.getContext().asDouble(scope, right);
	}
}
