package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class MapValueHandler implements Prototype {
	public static final MapValueHandler INSTANCE = new MapValueHandler();

	@NotNull
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	private static Map<String, Object> map(Object self) {
		return (Map<String, Object>) self;
	}

	@Override
	public String getPrototypeName() {
		return "object";
	}

	@Override
	public Object get(Scope scope, Object self, String name) {
		var v = map(self).get(name);
		return v == Special.NULL ? null : v;
	}

	@Override
	public boolean set(Scope scope, Object self, String name, @Nullable Object value) {
		map(self).put(name, value == null ? Special.NULL : value);
		return true;
	}

	@Override
	public boolean delete(Scope scope, Object self, String name) {
		map(self).remove(name);
		return true;
	}

	@Override
	public Collection<?> keys(Scope scope, Object self) {
		return map(self).keySet();
	}

	@Override
	public Collection<Object> values(Scope scope, Object self) {
		return map(self).values();
	}

	@Override
	public Collection<Map.Entry<String, Object>> entries(Scope scope, Object self) {
		return map(self).entrySet();
	}
}
