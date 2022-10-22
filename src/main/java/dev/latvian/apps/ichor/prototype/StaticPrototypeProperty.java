package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface StaticPrototypeProperty extends PrototypeProperty {
	Object get(Context cx, Scope scope);

	@Override
	default Object get(Context cx, Scope scope, Object self) {
		return get(cx, scope);
	}
}
