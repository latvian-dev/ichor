package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

public class AstMod extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('%');
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return evalDouble(cx, scope);
	}

	@Override
	public double evalDouble(Context cx, Scope scope) {
		return cx.asDouble(scope, left) % cx.asDouble(scope, right);
	}
}
