package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeFunction extends Callable {
	@Override
	Object call(Scope scope, Object self, Object[] args);
}
