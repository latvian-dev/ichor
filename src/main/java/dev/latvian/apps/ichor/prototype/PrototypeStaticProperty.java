package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PrototypeStaticProperty extends PrototypeProperty {
	Object get(Scope scope);

	default boolean set(Scope scope, @Nullable Object value) {
		return false;
	}

	@Override
	default Object get(Scope scope, Object self) {
		return get(scope);
	}

	@Override
	default boolean set(Scope scope, Object self, @Nullable Object value) {
		return set(scope, value);
	}
}
