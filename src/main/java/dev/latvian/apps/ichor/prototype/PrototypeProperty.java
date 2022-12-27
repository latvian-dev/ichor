package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PrototypeProperty {
	Object get(Context cx, Scope scope, Object self);

	default boolean set(Context cx, Scope scope, Object self, @Nullable Object value) {
		return false;
	}
}
