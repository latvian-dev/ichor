package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.WrappedObject;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArrayJS implements WrappedObject {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("Array") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return args.length == 0 ? new ArrayList<>() : new ArrayList<>(Arrays.asList(args));
		}
	};

	public final Object self;

	public ArrayJS(Object self) {
		this.self = self;
	}

	@Override
	public Object unwrap() {
		return self;
	}

	@Override
	public Prototype getPrototype(Context cx, Scope scope) {
		return PROTOTYPE;
	}

	@Override
	public void asString(Context cx, Scope scope, StringBuilder builder, boolean escape) {
		builder.append('[');

		int size = Array.getLength(self);

		if (size == 0) {
			builder.append(']');
			return;
		}

		for (int i = 0; i < size; i++) {
			if (i > 0) {
				builder.append(',');
				builder.append(' ');
			}

			cx.asString(scope, Array.get(self, i), builder, true);
		}

		builder.append(']');
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		return name.equals("length") ? Array.getLength(self) : Special.NOT_FOUND;
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, int index) {
		return Array.get(self, index);
	}

	@Override
	public boolean set(Context cx, Scope scope, int index, @Nullable Object value) {
		Array.set(self, index, value);
		return true;
	}

	@Override
	public Collection<?> keys(Context cx, Scope scope) {
		int size = Array.getLength(self);

		if (size == 0) {
			return List.of();
		}

		var keys = new ArrayList<Integer>(size);

		for (int i = 0; i < size; i++) {
			keys.add(i);
		}

		return keys;
	}

	@Override
	public Collection<?> values(Context cx, Scope scope) {
		int size = Array.getLength(self);

		if (size == 0) {
			return List.of();
		}

		var values = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			values.add(Array.get(self, i));
		}

		return values;
	}

	@Override
	public Collection<?> entries(Context cx, Scope scope) {
		int size = Array.getLength(self);

		if (size == 0) {
			return List.of();
		}

		var entries = new ArrayList<List<Object>>();

		for (int i = 0; i < size; i++) {
			entries.add(List.of(i, Array.get(self, i)));
		}

		return entries;
	}
}
