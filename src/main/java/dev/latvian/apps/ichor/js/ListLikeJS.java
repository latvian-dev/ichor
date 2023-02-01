package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.PrototypeWrappedObject;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

public interface ListLikeJS extends PrototypeWrappedObject {
	Functions.Bound<ListLikeJS> PUSH = (cx, scope, self, args) -> {
		int len = self.getLength();

		for (int i = 0; i < args.length; i++) {
			self.addAt(len + i, args[i]);
		}

		return self;
	};

	Functions.Bound<ListLikeJS> POP = (cx, scope, self, args) -> {
		int len = self.getLength();
		var e = self.getAt(len - 1);
		self.deleteAt(len - 1);
		return e;
	};

	Functions.Bound<ListLikeJS> SHIFT = (cx, scope, self, args) -> {
		var e = self.getAt(0);
		self.deleteAt(0);
		return e;
	};

	Functions.Bound<ListLikeJS> UNSHIFT = (cx, scope, self, args) -> {
		for (int i = 0; i < args.length; i++) {
			self.addAt(i, args[i]);
		}

		return self;
	};

	Functions.Bound<ListLikeJS> FOREACH = (cx, scope, self, args) -> {
		var func = (Callable) args[0];
		int len = self.getLength();

		for (int i = 0; i < len; i++) {
			func.call(cx, scope, new Object[]{self.getAt(i), i, self}, false);
		}

		return self;
	};

	int getLength();

	Object getAt(int index);

	default void setAt(int index, Object value) {
		throw new UnsupportedOperationException();
	}

	default void addAt(int index, Object value) {
		throw new UnsupportedOperationException();
	}

	default void deleteAt(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Object get(Context cx, Scope scope, int index) {
		return getAt(index);
	}

	@Override
	default boolean set(Context cx, Scope scope, int index, @Nullable Object value) {
		setAt(index, value);
		return true;
	}

	@Override
	default boolean delete(Context cx, Scope scope, int index) {
		deleteAt(index);
		return true;
	}

	@Nullable
	default Object getListLike(String name) {
		return switch (name) {
			case "length" -> getLength();
			case "push" -> PUSH.with(this);
			case "pop" -> POP.with(this);
			case "shift" -> SHIFT.with(this);
			case "unshift" -> UNSHIFT.with(this);
			case "forEach" -> FOREACH.with(this);
			default -> null;
		};
	}
}
