package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PrototypeStaticProperty extends PrototypeProperty {
	Object get(Context cx, Scope scope);

	default boolean set(Context cx, Scope scope, @Nullable Object value) {
		return false;
	}

	@Override
	default Object get(Context cx, Scope scope, Object self) {
		return get(cx, scope);
	}

	@Override
	default boolean set(Context cx, Scope scope, Object self, @Nullable Object value) {
		return set(cx, scope, value);
	}
}
