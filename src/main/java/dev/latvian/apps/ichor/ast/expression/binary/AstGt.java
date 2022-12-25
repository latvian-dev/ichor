package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstGt extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('>');
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return scope.getContext().compareTo(scope, left, right) > 0;
	}
}
