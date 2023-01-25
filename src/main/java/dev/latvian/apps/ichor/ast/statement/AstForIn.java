package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

import java.util.ArrayList;
import java.util.Collection;

public class AstForIn extends AstForOf {
	@Override
	protected String appendKeyword() {
		return " in ";
	}

	@Override
	protected Collection<?> getIterable(Context cx, Scope scope, Object from) {
		if (from instanceof Iterable<?> self) {
			var keys = new ArrayList<Integer>();

			int i = 0;

			for (var ignored : self) {
				keys.add(i++);
			}

			return keys;
		}

		return cx.wrap(scope, from).keys(cx, scope);
	}
}
