package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.WrappedObject;
import org.jetbrains.annotations.Nullable;

public interface PrototypeWrappedObject extends WrappedObject, PrototypeSupplier {
	@Override
	@Nullable
	default Object get(Context cx, Scope scope, String name) {
		return getPrototype(cx, scope).get(cx, scope, unwrap(), name);
	}

	@Override
	default boolean set(Context cx, Scope scope, String name, @Nullable Object value) {
		return getPrototype(cx, scope).set(cx, scope, unwrap(), name, value);
	}

	@Override
	default boolean delete(Context cx, Scope scope, String name) {
		return getPrototype(cx, scope).delete(cx, scope, unwrap(), name);
	}

	@Override
	@Nullable
	default Object get(Context cx, Scope scope, int index) {
		return getPrototype(cx, scope).get(cx, scope, unwrap(), index);
	}

	@Override
	default boolean set(Context cx, Scope scope, int index, @Nullable Object value) {
		return getPrototype(cx, scope).set(cx, scope, unwrap(), index, value);
	}

	@Override
	default boolean delete(Context cx, Scope scope, int index) {
		return getPrototype(cx, scope).delete(cx, scope, unwrap(), index);
	}
}
