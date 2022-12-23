package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeAsString {
	void asString(Scope scope, Object self, StringBuilder builder);
}
