package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeToString {
	PrototypeToString DEFAULT = (cx, scope, self) -> self.toString();

	String toString(Context cx, Scope scope, Object self);
}
