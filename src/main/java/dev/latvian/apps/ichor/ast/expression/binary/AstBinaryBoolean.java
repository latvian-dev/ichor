package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

public abstract class AstBinaryBoolean extends AstBinary {
	@Override
	public Object eval(Context cx, Scope scope) {
		return evalBoolean(cx, scope);
	}

	@Override
	public double evalDouble(Context cx, Scope scope) {
		return evalBoolean(cx, scope) ? 1D : 0D;
	}

	@Override
	public int evalInt(Context cx, Scope scope) {
		return evalBoolean(cx, scope) ? 1 : 0;
	}

	@Override
	public abstract boolean evalBoolean(Context cx, Scope scope);

	@Override
	public void evalString(Context cx, Scope scope, StringBuilder builder) {
		builder.append(evalBoolean(cx, scope));
	}
}
