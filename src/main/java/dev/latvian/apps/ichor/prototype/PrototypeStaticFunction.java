package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeStaticFunction extends PrototypeFunction {
	Object call(Scope scope, Object[] args);

	@Override
	default Object call(Scope scope, Object self, Object[] args) {
		return call(scope, args);
	}
}
