package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeSupplier {
	Prototype<?> getPrototype(Scope scope);
}
