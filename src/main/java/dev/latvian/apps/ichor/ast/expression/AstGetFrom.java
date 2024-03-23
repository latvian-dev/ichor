package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import org.jetbrains.annotations.Nullable;

public abstract class AstGetFrom extends AstGetBase {
	public Object from;

	public AstGetFrom(Object from) {
		this.from = from;
	}

	@Nullable
	public Object evalSelf(Scope scope) {
		return scope.eval(from);
	}

	@Override
	public Object optimize(Parser parser) {
		from = parser.optimize(from);
		return this;
	}
}
