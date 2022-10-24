package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Special;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface PrototypeIndexedValueHandler {
	default Object get(Context cx, int index, @Nullable Object self) {
		return Special.NOT_FOUND;
	}

	default boolean set(Context cx, int index, @Nullable Object self, @Nullable Object value) {
		return false;
	}

	default boolean delete(Context cx, int index, @Nullable Object self) {
		return false;
	}

	default int length(Context cx, @Nullable Object self) {
		return 0;
	}

	default Collection<Object> values(Context cx, @Nullable Object self) {
		return Set.of();
	}
}
