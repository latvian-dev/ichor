package dev.latvian.apps.ichor.lang.js.type;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ListJS extends Prototype<List> {
	public static final Functions.Bound<List> PUSH = (cx, scope, self, args) -> {
		int len = self.size();

		for (int i = 0; i < args.length; i++) {
			self.add(len + i, args[i]);
		}

		return self;
	};

	public static final Functions.Bound<List> POP = (cx, scope, self, args) -> self.remove(self.size() - 1);
	public static final Functions.Bound<List> SHIFT = (cx, scope, self, args) -> self.remove(0);

	public static final Functions.Bound<List> UNSHIFT = (cx, scope, self, args) -> {
		for (int i = 0; i < args.length; i++) {
			self.add(i, args[i]);
		}

		return self;
	};

	public ListJS(Context cx) {
		super(cx, List.class);
	}

	@Override
	@Nullable
	public Object getLocal(Context cx, Scope scope, List self, String name) {
		return switch (name) {
			case "length" -> self.size();
			case "push" -> PUSH.with(self);
			case "pop" -> POP.with(self);
			case "shift" -> SHIFT.with(self);
			case "unshift" -> UNSHIFT.with(self);
			case "forEach" -> IterableJS.FOR_EACH.with(self);
			default -> super.getStatic(cx, scope, name);
		};
	}

	@Override
	public int getLength(Context cx, Scope scope, Object self) {
		return ((List) self).size();
	}

	@Override
	@Nullable
	public Object getLocal(Context cx, Scope scope, List self, int index) {
		return self.get(index);
	}

	@Override
	public boolean setLocal(Context cx, Scope scope, List self, int index, @Nullable Object value) {
		self.set(index, cast(value));
		return true;
	}

	@Override
	public boolean deleteLocal(Context cx, Scope scope, List self, int index) {
		self.remove(index);
		return true;
	}
}
