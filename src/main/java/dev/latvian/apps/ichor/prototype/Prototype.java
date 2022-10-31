package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.js.NumberJS;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;

public interface Prototype extends PrototypeSupplier {

	@Override
	default Prototype getPrototype() {
		return this;
	}

	String getPrototypeName();

	default Object construct(Scope scope, Object[] args, @Nullable Object self) {
		return Special.NOT_FOUND;
	}

	default Object call(Scope scope, Object[] args, @Nullable Object self) {
		return Special.NOT_FOUND;
	}

	@Nullable
	default Object get(Scope scope, String name, @Nullable Object self) {
		return Special.NOT_FOUND;
	}

	default boolean has(Scope scope, String name, @Nullable Object self) {
		return false;
	}

	default boolean set(Scope scope, String name, @Nullable Object self, @Nullable Object value) {
		return false;
	}

	default boolean delete(Scope scope, String name, @Nullable Object self) {
		return false;
	}

	default Iterator<?> keyIterator(Scope scope, @Nullable Object self) {
		return Collections.emptyIterator();
	}

	default Iterator<?> valueIterator(Scope scope, @Nullable Object self) {
		return Collections.emptyIterator();
	}

	default Iterator<?> entryIterator(Scope scope, @Nullable Object self) {
		return Collections.emptyIterator();
	}

	default int getMemberCount(Scope scope, @Nullable Object self) {
		return 0;
	}

	default boolean isArrayPrototype() {
		return false;
	}

	@Nullable
	default Object get(Scope scope, int index, @Nullable Object self) {
		return Special.NOT_FOUND;
	}

	default boolean has(Scope scope, int index, @Nullable Object self) {
		return false;
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
