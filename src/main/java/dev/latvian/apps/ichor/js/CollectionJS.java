package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.java.JavaClassPrototype;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("rawtypes")
public class CollectionJS<T extends Collection> extends IterableJS<T> {
	public CollectionJS(T self, JavaClassPrototype prototype) {
		super(self, prototype);
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		return name.equals("length") ? self.size() : super.get(cx, scope, name);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public boolean set(Context cx, Scope scope, int index, @Nullable Object value) {
		if (index >= self.size()) {
			self.add(value == null ? Special.NULL : value);
			return true;
		}

		return false;
	}

	@Override
	public Collection<?> keys(Context cx, Scope scope) {
		var keys = new ArrayList<Integer>(self.size());

		for (int i = 0; i < self.size(); i++) {
			keys.add(i);
		}

		return keys;
	}

	@Override
	public Collection<?> values(Context cx, Scope scope) {
		return self;
	}

	@Override
	public Collection<?> entries(Context cx, Scope scope) {
		var entries = new ArrayList<List<Object>>();

		int i = 0;

		for (var o : self) {
			entries.add(List.of(i++, o));
		}

		return entries;
	}
}
