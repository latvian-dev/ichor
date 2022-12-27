package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeStaticFunction extends PrototypeFunction {
	Object call(Context cx, Scope scope, Object[] args);

	@Override
	default Object call(Context cx, Scope scope, Object self, Object[] args) {
		return call(cx, scope, args);
	}
}
