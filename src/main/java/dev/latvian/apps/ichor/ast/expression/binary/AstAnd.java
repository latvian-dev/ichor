package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstAnd extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("&&");
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return scope.getContext().asBoolean(scope, left) && scope.getContext().asBoolean(scope, right);
	}
}
