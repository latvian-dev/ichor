package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface StaticPrototypeFunction extends PrototypeFunction {
	Object invoke(Context cx, Scope scope, Object[] args);

	@Override
	default Object invoke(Context cx, Scope scope, Object self, Object[] args) {
		return invoke(cx, scope, args);
	}
}
