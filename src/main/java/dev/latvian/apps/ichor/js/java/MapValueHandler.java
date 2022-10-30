package dev.latvian.apps.ichor.js.java;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.prototype.PrototypeNamedValueHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class MapValueHandler implements PrototypeNamedValueHandler {
	public static final MapValueHandler INSTANCE = new MapValueHandler();

	@NotNull
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	private static Map<String, Object> map(@Nullable Object self) {
		return (Map<String, Object>) self;
	}

	@Override
	public Object get(Scope scope, String name, @Nullable Object self) {
		var v = map(self).get(name);
		return v == Special.NULL ? null : v;
	}

	@Override
	public boolean set(Scope scope, String name, @Nullable Object self, @Nullable Object value) {
		map(self).put(name, value == null ? Special.NULL : value);
		return true;
	}

	@Override
	public boolean delete(Scope scope, String name, @Nullable Object self) {
		map(self).remove(name);
		return true;
	}

	@Override
	public Collection<String> keys(Scope scope, @Nullable Object self) {
		return map(self).keySet();
	}

	@Override
	public Collection<Object> values(Scope scope, @Nullable Object self) {
		return map(self).values();
	}

	@Override
	public Collection<Map.Entry<String, Object>> entries(Scope scope, @Nullable Object self) {
		return map(self).entrySet();
	}
}
