package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

public class AstOr extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("||");
	}

	@Override
	public boolean evalBoolean(Context cx, Scope scope) {
		return cx.asBoolean(scope, left) || cx.asBoolean(scope, right);
	}
}
