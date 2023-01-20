package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PrototypeStaticProperty {
	Object get(Context cx, Scope scope);

	default boolean set(Context cx, Scope scope, @Nullable Object value) {
		return false;
	}
}
