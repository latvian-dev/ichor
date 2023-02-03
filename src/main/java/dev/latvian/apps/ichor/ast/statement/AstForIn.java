package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class AstForIn extends AstForOf {
	@Override
	protected String appendKeyword() {
		return " in ";
	}

	@Override
	protected Collection<?> getIterable(Context cx, Scope scope, Object self) {
		if (self instanceof Collection<?> c) {
			var keys = new Object[c.size()];

			for (int i = 0; i < keys.length; i++) {
				keys[i] = i;
			}

			return Arrays.asList(keys);
		} else if (self instanceof Iterable<?> itr) {
			var keys = new ArrayList<Integer>();

			int i = 0;

			for (var ignored : itr) {
				keys.add(i++);
			}

			return keys;
		} else {
			var p = cx.getPrototype(scope, self);
			return p.keys(cx, scope, p.cast(self));
		}
	}
}
