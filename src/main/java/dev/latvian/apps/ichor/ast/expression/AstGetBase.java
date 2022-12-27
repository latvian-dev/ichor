package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

public abstract class AstGetBase extends AstExpression {
	public abstract void set(Context cx, Scope scope, Object value);

	public abstract boolean delete(Context cx, Scope scope);
}
