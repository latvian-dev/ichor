package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeConstructor {
	Object construct(Scope scope, Object[] args, boolean hasNew);
}
