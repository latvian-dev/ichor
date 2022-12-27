package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

public class AstBitwiseOr extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('|');
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return evalInt(cx, scope);
	}

	@Override
	public double evalDouble(Context cx, Scope scope) {
		return evalInt(cx, scope);
	}

	@Override
	public int evalInt(Context cx, Scope scope) {
		return cx.asInt(scope, left) | cx.asInt(scope, right);
	}

	@Override
	public boolean evalBoolean(Context cx, Scope scope) {
		return cx.asBoolean(scope, left) | cx.asBoolean(scope, right);
	}
}
