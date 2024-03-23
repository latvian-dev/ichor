package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PrototypeStaticProperty {
	Object get(Scope scope);

	default boolean set(Scope scope, @Nullable Object value) {
		return false;
	}
}
