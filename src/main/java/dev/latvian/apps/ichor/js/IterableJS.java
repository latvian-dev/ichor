package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("rawtypes")
public class IterableJS<T extends Iterable> extends JavaObjectJS<T> {
	public IterableJS(T self, Prototype prototype) {
		super(self, prototype);
	}

	@Override
	public void asString(Context cx, Scope scope, StringBuilder builder, boolean escape) {
		builder.append('[');

		boolean first = true;

		for (var o : self) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
				builder.append(' ');
			}

			cx.asString(scope, o, builder, true);
		}

		builder.append(']');
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		if (name.equals("length")) {
			int i = 0;

			for (var ignored : self) {
				i++;
			}

			return i;
		} else {
			return super.get(cx, scope, name);
		}
	}

	@Override
	public Collection<?> keys(Context cx, Scope scope) {
		var keys = new ArrayList<Integer>();

		int i = 0;

		for (var ignored : self) {
			keys.add(i++);
		}

		return keys;
	}

	@Override
	public Collection<?> values(Context cx, Scope scope) {
		var values = new ArrayList<>();

		for (var o : self) {
			values.add(o);
		}

		return values;
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
