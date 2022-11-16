package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeFunction extends Prototype {
	@Override
	default String getPrototypeName() {
		return "<prototype function>";
	}

	@Override
	Object call(Scope scope, Object self, Evaluable[] args);
}
