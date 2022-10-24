package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;

@FunctionalInterface
public interface PrototypeAsString {
	String asString(Context cx, Object self);
}
