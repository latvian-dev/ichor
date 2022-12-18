package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.js.NumberJS;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public interface Prototype extends PrototypeSupplier, Callable {

	@Override
	default Prototype getPrototype(Context cx) {
		return this;
	}

	String getPrototypeName();

	@Override
	default Object call(Scope scope, Object self, Object[] args) {
		return Special.NOT_FOUND;
	}

	@Nullable
	default Object get(Scope scope, Object self, String name) {
		return Special.NOT_FOUND;
	}

	default boolean set(Scope scope, Object self, String name, @Nullable Object value) {
		return false;
	}

	default boolean delete(Scope scope, Object self, String name) {
		return false;
	}

	default Collection<?> keys(Scope scope, Object self) {
		return Collections.emptySet();
	}

	default Collection<?> values(Scope scope, Object self) {
		return Collections.emptySet();
	}

	default Collection<?> entries(Scope scope, Object self) {
		return Collections.emptySet();
	}

	default int getMemberCount(Scope scope, Object self) {
		return 0;
	}

	default boolean isArrayPrototype() {
		return false;
	}

	@Nullable
	default Object get(Scope scope, Object self, int index) {
		return Special.NOT_FOUND;
	}

	default boolean set(Scope scope, Object self, int index, @Nullable Object value) {
		return false;
	}

	default boolean delete(Scope scope, Object self, int index) {
		return false;
	}

	default void toString(Scope scope, Object self, StringBuilder builder) {
		builder.append(self);
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
