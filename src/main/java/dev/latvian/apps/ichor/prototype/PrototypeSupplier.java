package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeSupplier extends Evaluable {
	Prototype getPrototype();

	@Override
	default Object eval(Scope scope) {
		return getPrototype();
	}
}
