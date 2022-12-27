package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;

import java.util.Collection;

public class AstForIn extends AstForOf {
	@Override
	protected String appendKeyword() {
		return " in ";
	}

	@Override
	protected Collection<?> getIterable(Context cx, Scope scope, Prototype prototype, Object from) {
		return prototype.keys(cx, scope, from);
	}
}
