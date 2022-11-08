package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstBitwiseOr extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('|');
	}

	@Override
	public Object eval(Scope scope) {
		return evalInt(scope);
	}

	@Override
	public int evalInt(Scope scope) {
		return scope.getContext().asInt(scope, left) | scope.getContext().asInt(scope, right);
	}
}