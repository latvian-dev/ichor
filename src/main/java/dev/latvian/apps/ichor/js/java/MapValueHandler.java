package dev.latvian.apps.ichor.js.java;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.prototype.PrototypeNamedValueHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class MapValueHandler implements PrototypeNamedValueHandler {
	public static final MapValueHandler INSTANCE = new MapValueHandler();

	@NotNull
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	private static Map<String, Object> map(@Nullable Object self) {
		return (Map<String, Object>) self;
	}

	@Override
	public Object get(Context cx, String name, @Nullable Object self) {
		var v = map(self).get(name);
		return v == Special.NULL ? null : v;
	}

	@Override
	public boolean set(Context cx, String name, @Nullable Object self, @Nullable Object value) {
		map(self).put(name, value == null ? Special.NULL : value);
		return true;
	}

	@Override
	public boolean delete(Context cx, String name, @Nullable Object self) {
		map(self).remove(name);
		return true;
	}

	@Override
	public Set<String> keys(Context cx, @Nullable Object self) {
		return Set.of();
	}

	@Override
	public Set<Object> values(Context cx, @Nullable Object self) {
		return Set.of();
	}

	@Override
	public Set<Map.Entry<String, Object>> entries(Context cx, @Nullable Object self) {
		return Set.of();
	}
}
