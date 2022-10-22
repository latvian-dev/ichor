package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeFunction {
	Object invoke(Context cx, Scope scope, Object self, Object[] args);
}
