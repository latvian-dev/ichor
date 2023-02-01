package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

public class ArrayJS extends JavaObjectJS<Object> implements ListLikeJS {
	public ArrayJS(Object self, Prototype prototype) {
		super(self, prototype);
	}

	@Override
	public int getLength() {
		return Array.getLength(self);
	}

	@Override
	public Object getAt(int index) {
		return Array.get(self, index);
	}

	@Override
	public void setAt(int index, Object value) {
		Array.set(self, index, value);
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		var ll = getListLike(name);
		return ll != null ? ll : super.get(cx, scope, name);
	}
}
