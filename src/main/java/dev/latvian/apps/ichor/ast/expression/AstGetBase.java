package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;

public abstract class AstGetBase extends AstExpression {
	public abstract void set(Scope scope, Object value);

	public abstract boolean delete(Scope scope);
}
