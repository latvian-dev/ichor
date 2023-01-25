package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import org.jetbrains.annotations.Nullable;

public interface Prototype extends PrototypeSupplier {
	@Override
	default Prototype getPrototype(Context cx, Scope scope) {
		return this;
	}

	String getPrototypeName();

	@Nullable
	default Object get(Context cx, Scope scope, Object self, String name) {
		return Special.NOT_FOUND;
	}

	default boolean set(Context cx, Scope scope, Object self, String name, @Nullable Object value) {
		return false;
	}

	default boolean delete(Context cx, Scope scope, Object self, String name) {
		return false;
	}

	@Nullable
	default Object get(Context cx, Scope scope, Object self, int index) {
		return Special.NOT_FOUND;
	}

	default boolean set(Context cx, Scope scope, Object self, int index, @Nullable Object value) {
		return false;
	}

	default boolean delete(Context cx, Scope scope, Object self, int index) {
		return false;
	}
}
