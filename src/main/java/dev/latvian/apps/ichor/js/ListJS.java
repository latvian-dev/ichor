package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.java.JavaClassPrototype;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("rawtypes")
public class ListJS<T extends List> extends CollectionJS<T> {
	public ListJS(T self, JavaClassPrototype prototype) {
		super(self, prototype);
	}

	@Override
	public Object get(Context cx, Scope scope, int index) {
		var v = self.get(index);
		return v == Special.NULL ? null : v;
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public boolean set(Context cx, Scope scope, int index, @Nullable Object value) {
		if (index >= self.size()) {
			self.add(value == null ? Special.NULL : value);
		} else {
			self.set(index, value == null ? Special.NULL : value);
		}

		return true;
	}

	@Override
	public boolean delete(Context cx, Scope scope, int index) {
		self.remove(index);
		return true;
	}
}
