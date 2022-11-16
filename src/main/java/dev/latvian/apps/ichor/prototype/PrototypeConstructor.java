package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeConstructor {
	Object construct(Scope scope, Evaluable[] args, boolean hasNew);
}
