package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;

public class AstNc extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("??");
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		var l = cx.eval(scope, left);
		return Special.isInvalid(l) ? cx.eval(scope, right) : l;
	}
}
