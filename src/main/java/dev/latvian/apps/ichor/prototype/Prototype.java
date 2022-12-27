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
	default Prototype getPrototype(Context cx, Scope scope) {
		return this;
	}

	String getPrototypeName();

	@Override
	default Object call(Context cx, Scope scope, Object self, Object[] args) {
		return Special.NOT_FOUND;
	}

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

	default Collection<?> keys(Context cx, Scope scope, Object self) {
		return Collections.emptySet();
	}

	default Collection<?> values(Context cx, Scope scope, Object self) {
		return Collections.emptySet();
	}

	default Collection<?> entries(Context cx, Scope scope, Object self) {
		return Collections.emptySet();
	}

	default int getMemberCount(Context cx, Scope scope, Object self) {
		return 0;
	}

	default boolean isArrayPrototype() {
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

	default void asString(Context cx, Scope scope, Object self, StringBuilder builder, boolean escape) {
		builder.append(self);
	}

	default Number asNumber(Context cx, Scope scope, Object self) {
		return NumberJS.ONE;
	}

	default boolean asBoolean(Context cx, Scope scope, Object self) {
		return true;
	}
}
