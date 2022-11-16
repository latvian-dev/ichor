package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeSupplier extends Evaluable {
	Prototype getPrototype(Context cx);

	@Override
	default Object eval(Scope scope) {
		return getPrototype(scope.getContext());
	}
}
