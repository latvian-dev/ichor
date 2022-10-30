package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface PrototypeNamedValueHandler {
	default Object get(Scope scope, String name, Object self) {
		return Special.NOT_FOUND;
	}

	default boolean set(Scope scope, String name, Object self, @Nullable Object value) {
		return false;
	}

	default boolean delete(Scope scope, String name, Object self) {
		return false;
	}

	default Collection<String> keys(Scope scope, Object self) {
		return Set.of();
	}

	default Collection<Object> values(Scope scope, Object self) {
		return Set.of();
	}

	default Collection<Map.Entry<String, Object>> entries(Scope scope, Object self) {
		return Set.of();
	}
}
