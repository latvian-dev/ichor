package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;

@FunctionalInterface
public interface Callable {
	Object call(Scope scope, Object self, Object[] args);

	default Object construct(Scope scope, Object[] args) {
		return Special.NOT_FOUND;
	}
}
