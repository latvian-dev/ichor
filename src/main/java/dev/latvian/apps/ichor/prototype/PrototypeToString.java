package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeToString {
	void toString(Scope scope, Object self, StringBuilder builder);
}
