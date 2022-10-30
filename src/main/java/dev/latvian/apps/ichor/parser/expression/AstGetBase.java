package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Scope;

public abstract class AstGetBase extends AstExpression {
	public abstract void set(Scope scope, Object value);
}
