package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeAsString {
	void asString(Context cx, Scope scope, Object self, StringBuilder builder, boolean escape);
}
