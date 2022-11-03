package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;

public abstract class AstGetFrom extends AstGetBase {
	public final Object from;

	public AstGetFrom(Object from) {
		this.from = from;
	}

	public abstract Object evalKey(Scope scope);
}
