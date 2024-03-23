package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstGte extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append(">=");
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return scope.compareTo(scope.eval(left), scope.eval(right)) >= 0;
	}
}
