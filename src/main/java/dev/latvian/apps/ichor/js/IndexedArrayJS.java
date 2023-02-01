package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

public class IndexedArrayJS extends JavaObjectJS<Object[]> implements ListLikeJS {
	public IndexedArrayJS(Object[] self, Prototype prototype) {
		super(self, prototype);
	}

	@Override
	public int getLength() {
		return self.length;
	}

	@Override
	public Object getAt(int index) {
		return self[index];
	}

	@Override
	public void setAt(int index, Object value) {
		self[index] = value;
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		var ll = getListLike(name);
		return ll != null ? ll : super.get(cx, scope, name);
	}
}
