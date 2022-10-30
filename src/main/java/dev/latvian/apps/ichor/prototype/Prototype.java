package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
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
	default Object get(Scope scope, String name, @Nullable Object self) {
		return Special.NOT_FOUND;
	}

	default boolean set(Scope scope, String name, @Nullable Object self, @Nullable Object value) {
		return false;
	}

	default boolean delete(Scope scope, String name, @Nullable Object self) {
		return false;
	}

	@Nullable
	default Object get(Scope scope, int index, @Nullable Object self) {
		return Special.NOT_FOUND;
	}

	default boolean set(Scope scope, int index, @Nullable Object self, @Nullable Object value) {
		return false;
	}

	default boolean delete(Scope scope, int index, @Nullable Object self) {
		return false;
	}

	default String asString(Scope scope, Object self) {
		return self.toString();
	}

	default Number asNumber(Scope scope, Object self) {
		return NumberJS.ONE;
	}

	default Boolean asBoolean(Scope scope, Object self) {
		return Boolean.TRUE;
	}
}
