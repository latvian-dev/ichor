package dev.latvian.apps.ichor.js.type;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked"})
public class SetJS extends Prototype<Set> {
	public static final Functions.Bound<Set> ADD = (cx, scope, self, args) -> {
		self.add(args[0]);
		return self;
	};

	public static final Functions.Bound<Set> CLEAR = (cx, scope, self, args) -> {
		self.clear();
		return Special.UNDEFINED;
	};

	public static final Functions.Bound<Set> DELETE = (cx, scope, self, args) -> self.remove(args[0]);

	public static final Functions.Bound<Set> ENTRIES = (cx, scope, self, args) -> {
		var entries = new List[self.size()];
		int i = 0;

		for (var entry : self) {
			entries[i] = List.of(entry, entry);
			i++;
		}

		return Arrays.asList(entries).iterator();
	};

	public static final Functions.Bound<Set> HAS = (cx, scope, self, args) -> self.contains(args[0]);
	public static final Functions.Bound<Set> VALUES = (cx, scope, self, args) -> self.iterator();

	public SetJS(Context cx) {
		super(cx, "Set", Set.class);
	}

	@Override
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		return new LinkedHashSet<>();
	}

	@Override
	public Collection<?> keys(Context cx, Scope scope, Set self) {
		return self;
	}

	@Override
	public Collection<?> values(Context cx, Scope scope, Set self) {
		return self;
	}

	@Override
	@Nullable
	public Object getLocal(Context cx, Scope scope, Set self, String name) {
		return switch (name) {
			case "length", "size" -> self.size();
			case "add" -> ADD.with(self);
			case "clear" -> CLEAR.with(self);
			case "delete" -> DELETE.with(self);
			case "entries" -> ENTRIES.with(self);
			case "has" -> HAS.with(self);
			case "keys", "values" -> VALUES.with(self);
			case "forEach" -> IterableJS.FOR_EACH.with(self);
			default -> super.getStatic(cx, scope, name);
		};
	}

	@Override
	public Collection<?> entries(Context cx, Scope scope, Set self) {
		if (self.isEmpty()) {
			return List.of();
		} else if (self.size() == 1) {
			var value = self.iterator().next();
			return List.of(List.of(value, value));
		} else {
			var entries = new List[self.size()];
			int i = 0;

			for (var entry : self) {
				entries[i] = List.of(entry, entry);
				i++;
			}

			return Arrays.asList(entries);
		}
	}
}
