package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Special;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface PrototypeNamedValueHandler {
	default Object get(Context cx, String name, Object self) {
		return Special.NOT_FOUND;
	}

	default boolean set(Context cx, String name, Object self, @Nullable Object value) {
		return false;
	}

	default boolean delete(Context cx, String name, Object self) {
		return false;
	}

	default Set<String> keys(Context cx, Object self) {
		return Set.of();
	}

	default Set<Object> values(Context cx, Object self) {
		return Set.of();
	}

	default Set<Map.Entry<String, Object>> entries(Context cx, Object self) {
		return Set.of();
	}
}
