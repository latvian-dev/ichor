package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public abstract class AstBinaryBoolean extends AstBinary {
	@Override
	public Object eval(Scope scope) {
		return evalBoolean(scope);
	}

	@Override
	public double evalDouble(Scope scope) {
		return evalBoolean(scope) ? 1D : 0D;
	}

	@Override
	public int evalInt(Scope scope) {
		return evalBoolean(scope) ? 1 : 0;
	}

	@Override
	public abstract boolean evalBoolean(Scope scope);

	@Override
	public void evalString(Scope scope, StringBuilder builder) {
		builder.append(evalBoolean(scope));
	}
}
