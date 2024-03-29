package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstMod extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('%');
	}

	@Override
	public Object eval(Scope scope) {
		return evalDouble(scope);
	}

	@Override
	public double evalDouble(Scope scope) {
		return scope.asDouble(left) % scope.asDouble(right);
	}
}
