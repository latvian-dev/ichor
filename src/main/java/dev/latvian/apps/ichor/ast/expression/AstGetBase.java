package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.CallableAst;

public abstract class AstGetBase extends AstExpression implements CallableAst {
	public abstract void set(Scope scope, Object value);

	public abstract boolean delete(Scope scope);
}
