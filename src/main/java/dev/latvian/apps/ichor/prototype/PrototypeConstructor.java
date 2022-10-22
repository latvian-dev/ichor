package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeConstructor {
	Object construct(Context cx, Scope scope, Object[] args, boolean hasNew);
}
