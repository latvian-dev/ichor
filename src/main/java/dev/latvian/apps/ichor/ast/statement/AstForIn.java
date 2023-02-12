package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.util.IchorUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class AstForIn extends AstForOf {
	@Override
	protected String appendKeyword() {
		return " in ";
	}

	@Override
	@Nullable
	protected Iterator<?> getIterable(Context cx, Scope scope, Object self) {
		if (self instanceof Collection<?> c) {
			var keys = new Object[c.size()];

			for (int i = 0; i < keys.length; i++) {
				keys[i] = i;
			}

			return IchorUtils.iteratorOf(keys);
		} else if (self instanceof Iterable<?> itr) {
			var keys = new ArrayList<Integer>();

			int i = 0;

			for (var ignored : itr) {
				keys.add(i++);
			}

			return keys.iterator();
		} else {
			var p = cx.getPrototype(scope, self);
			return IchorUtils.iteratorOf(p.keys(cx, scope, p.cast(self)));
		}
	}
}
