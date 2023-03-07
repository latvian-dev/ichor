package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;

public abstract class AstTempExpression extends AstExpression {
	@Override
	public Object eval(Context cx, Scope scope) {
		return Special.NOT_FOUND;
	}
}
