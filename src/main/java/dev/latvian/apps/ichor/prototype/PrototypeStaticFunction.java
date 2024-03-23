package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeStaticFunction extends PrototypeStaticProperty, Callable {
	Object call(Scope scope, Object[] args);

	@Override
	default Object call(Scope scope, Object[] args, boolean hasNew) {
		return call(scope, args);
	}

	@Override
	default Object get(Scope scope) {
		return this;
	}
}
