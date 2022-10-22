package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeToBoolean {
	PrototypeToBoolean DEFAULT = (cx, scope, self) -> true;

	boolean toBoolean(Context cx, Scope scope, Object self);
}
