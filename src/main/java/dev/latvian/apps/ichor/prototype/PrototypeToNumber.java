package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeToNumber {
	PrototypeToNumber DEFAULT = (cx, scope, self) -> 1D;

	double toNumber(Context cx, Scope scope, Object self);
}
