package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstSneq extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("!==");
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return !scope.equals(scope.eval(left), scope.eval(right), true);
	}
}
