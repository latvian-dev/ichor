package dev.latvian.apps.ichor.type;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"rawtypes"})
public class CollectionJS extends Prototype<Collection> {
	public CollectionJS(Scope cx) {
		super(cx, Collection.class);
	}

	@Override
	@Nullable
	public Object getLocal(Scope scope, Collection self, String name) {
		return switch (name) {
			case "length" -> self.size();
			case "forEach" -> IterableJS.FOR_EACH.with(self);
			default -> super.getLocal(scope, self, name);
		};
	}

	@Override
	public int getLength(Scope scope, Object self) {
		return ((Collection) self).size();
	}

	@Override
	public Collection<?> keys(Scope scope, Collection self) {
		var keys = new Object[self.size()];

		for (int i = 0; i < self.size(); i++) {
			keys[i] = i;
		}

		return Arrays.asList(keys);
	}

	@Override
	public Collection<?> values(Scope scope, Collection self) {
		return self;
	}

	@Override
	public Collection<?> entries(Scope scope, Collection self) {
		var entries = new Object[self.size()];

		int i = 0;

		for (var o : self) {
			entries[i] = List.of(i, o);
			i++;
		}

		return Arrays.asList(entries);
	}
}
