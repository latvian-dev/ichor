package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface PrototypeIndexedValueHandler {
	default Object get(Scope scope, int index, @Nullable Object self) {
		return Special.NOT_FOUND;
	}

	default boolean set(Scope scope, int index, @Nullable Object self, @Nullable Object value) {
		return false;
	}

	default boolean delete(Scope scope, int index, @Nullable Object self) {
		return false;
	}

	default int length(Scope scope, @Nullable Object self) {
		return 0;
	}

	default Collection<Object> values(Scope scope, @Nullable Object self) {
		return Set.of();
	}
}
