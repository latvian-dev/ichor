package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeStaticFunction extends PrototypeStaticProperty, Callable {
	Object call(Context cx, Scope scope, Object[] args);

	@Override
	default Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		return call(cx, scope, args);
	}

	@Override
	default Object get(Context cx, Scope scope) {
		return this;
	}
}
