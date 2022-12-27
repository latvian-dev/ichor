package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListValueHandler implements Prototype {
	public static final ListValueHandler INSTANCE = new ListValueHandler();

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Collection<Object> collection(Object self) {
		return (Collection) self;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static List<Object> list(Object self) {
		return (List) self;
	}

	@Override
	public String getPrototypeName() {
		return "array";
	}

	@Override
	public Object get(Context cx, Scope scope, Object self, int index) {
		var v = list(self).get(index);
		return v == Special.NULL ? null : v;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public boolean set(Context cx, Scope scope, Object self, int index, @Nullable Object value) {
		var c = collection(self);

		if (index >= c.size()) {
			c.add(value == null ? Special.NULL : value);
		} else if (c instanceof List list) {
			list.set(index, value == null ? Special.NULL : value);
		}

		return true;
	}

	@Override
	public boolean delete(Context cx, Scope scope, Object self, int index) {
		list(self).remove(index);
		return true;
	}

	@Override
	public Collection<?> keys(Context cx, Scope scope, Object self) {
		int size = collection(self).size();
		var keys = new Integer[size];

		for (int i = 0; i < size; i++) {
			keys[i] = i;
		}

		return Arrays.asList(keys);
	}

	@Override
	public Collection<?> values(Context cx, Scope scope, Object self) {
		return collection(self);
	}

	@Override
	public Collection<?> entries(Context cx, Scope scope, Object self) {
		var c = collection(self);
		var entries = new Object[c.size()];
		int i = 0;

		for (var o : c) {
			entries[i] = new AbstractMap.SimpleEntry<>(i, o);
			i++;
		}

		return Arrays.asList(entries);
	}
}
