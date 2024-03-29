package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstLsh extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("<<");
	}

	@Override
	public Object eval(Scope scope) {
		return evalInt(scope);
	}

	@Override
	public double evalDouble(Scope scope) {
		return evalInt(scope);
	}

	@Override
	public int evalInt(Scope scope) {
		return scope.asInt(left) << scope.asInt(right);
	}
}
