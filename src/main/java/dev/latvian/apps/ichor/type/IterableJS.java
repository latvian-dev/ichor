package dev.latvian.apps.ichor.type;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"rawtypes"})
public class IterableJS extends Prototype<Iterable> {
	public static final Functions.Bound<Iterable> FOR_EACH = (scope, self, args) -> {
		var func = (Callable) args[0];
		int i = 0;

		for (var o : self) {
			func.call(scope, new Object[]{o, i, self}, false);
			i++;
		}

		return self;
	};

	public IterableJS(Scope cx) {
		super(cx, Iterable.class);
	}

	@Override
	public boolean asString(Scope scope, Iterable self, StringBuilder builder, boolean escape) {
		builder.append('[');

		boolean first = true;

		for (var o : self) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
				builder.append(' ');
			}

			scope.asString(o, builder, true);
		}

		builder.append(']');
		return true;
	}

	@Override
	@Nullable
	public Object getLocal(Scope scope, Iterable self, String name) {
		return switch (name) {
			case "length" -> {
				int i = 0;
				for (var ignored : self) {
					i++;
				}
				yield i;
			}
			case "forEach" -> FOR_EACH.with(self);
			default -> super.getLocal(scope, self, name);
		};
	}

	@Override
	public Collection<?> keys(Scope scope, Iterable self) {
		var keys = new ArrayList<Integer>();

		int i = 0;

		for (var ignored : self) {
			keys.add(i++);
		}

		return keys;
	}

	@Override
	public Collection<?> values(Scope scope, Iterable self) {
		var values = new ArrayList<>();

		for (var o : self) {
			values.add(o);
		}

		return values;
	}

	@Override
	public Collection<?> entries(Scope scope, Iterable self) {
		var entries = new ArrayList<List<Object>>();

		int i = 0;

		for (var o : self) {
			entries.add(List.of(i++, o));
		}

		return entries;
	}
}
