package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ListJS<T extends List> extends CollectionJS<T> implements ListLikeJS {
	public static Prototype createDefaultArrayPrototype() {
		return new Prototype("Array") {
			@Override
			public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
				return args.length == 0 ? new ArrayList<>() : new ArrayList<>(Arrays.asList(args));
			}
		};
	}

	public ListJS(T self, Prototype prototype) {
		super(self, prototype);
	}

	@Override
	public int getLength() {
		return self.size();
	}

	@Override
	public Object getAt(int index) {
		var v = self.get(index);
		return v == Special.NULL ? null : v;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setAt(int index, Object value) {
		if (index >= self.size()) {
			self.add(value == null ? Special.NULL : value);
		} else {
			self.set(index, value == null ? Special.NULL : value);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addAt(int index, Object value) {
		self.add(index, value == null ? Special.NULL : value);
	}

	@Override
	public void deleteAt(int index) {
		self.remove(index);
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		var ll = getListLike(name);
		return ll != null ? ll : super.get(cx, scope, name);
	}
}
