package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.js.NumberJS;
import org.jetbrains.annotations.Nullable;

public interface Prototype extends PrototypeSupplier {

	@Override
	default Prototype getPrototype() {
		return this;
	}

	String getPrototypeName();

	@Nullable
	default Object get(Context cx, String name, @Nullable Object self) {
		return Special.NOT_FOUND;
	}

	default boolean set(Context cx, String name, @Nullable Object self, @Nullable Object value) {
		return false;
	}

	default boolean delete(Context cx, String name, @Nullable Object self) {
		return false;
	}

	@Nullable
	default Object get(Context cx, int index, @Nullable Object self) {
		return Special.NOT_FOUND;
	}

	default boolean set(Context cx, int index, @Nullable Object self, @Nullable Object value) {
		return false;
	}

	default boolean delete(Context cx, int index, @Nullable Object self) {
		return false;
	}

	default String asString(Context cx, Object self) {
		return self.toString();
	}

	default Number asNumber(Context cx, Object self) {
		return NumberJS.ONE;
	}

	default Boolean asBoolean(Context cx, Object self) {
		return Boolean.TRUE;
	}
}
