package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

public class AstSeq extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("===");
	}

	@Override
	public boolean evalBoolean(Context cx, Scope scope) {
		return cx.equals(scope, cx.eval(scope, left), cx.eval(scope, right), true);
	}
}
