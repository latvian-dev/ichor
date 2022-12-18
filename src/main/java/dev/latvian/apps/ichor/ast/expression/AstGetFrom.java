package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import org.jetbrains.annotations.Nullable;

public abstract class AstGetFrom extends AstGetBase {
	public final Evaluable from;

	public AstGetFrom(Evaluable from) {
		this.from = from;
	}

	public abstract Object evalKey(Scope scope);

	@Override
	@Nullable
	public Object evalSelf(Scope scope) {
		return from.eval(scope);
	}
}
