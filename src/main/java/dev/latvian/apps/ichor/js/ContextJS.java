package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.WrappedObject;
import dev.latvian.apps.ichor.WrappedObjectFactory;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import dev.latvian.apps.ichor.util.NativeArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class ContextJS extends Context {
	public static final Prototype OBJECT_PROTOTYPE = new PrototypeBuilder("Object") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return new LinkedHashMap<>();
		}
	};

	public static final Prototype ARRAY_PROTOTYPE = new PrototypeBuilder("Array") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return args.length == 0 ? new ArrayList<>() : new ArrayList<>(Arrays.asList(args));
		}
	};

	private Executor timeoutExecutor, timeoutExecutorAfter;
	private Consumer<Scope> debuggerCallback;

	private final Prototype listPrototype, collectionPrototype, iterablePrototype, mapPrototype, arrayPrototype;

	public ContextJS() {
		timeoutExecutor = null;
		timeoutExecutorAfter = null;

		listPrototype = getClassPrototype(List.class);
		collectionPrototype = getClassPrototype(Collection.class);
		iterablePrototype = getClassPrototype(Iterable.class);
		mapPrototype = getClassPrototype(Map.class);
		arrayPrototype = getClassPrototype(Object[].class);
	}

	@Override
	public List<Prototype> getSafePrototypes() {
		return List.of(
				StringJS.PROTOTYPE,
				NumberJS.PROTOTYPE,
				BooleanJS.PROTOTYPE,
				OBJECT_PROTOTYPE,
				ARRAY_PROTOTYPE,
				MathJS.PROTOTYPE
		);
	}

	@Nullable
	public Executor getTimeoutExecutor() {
		if (timeoutExecutor == null) {
			timeoutExecutor = CompletableFuture.completedFuture(null).defaultExecutor();
		}

		return timeoutExecutor;
	}

	public void setTimeoutExecutor(Executor executor) {
		timeoutExecutor = executor;
	}

	@Nullable
	public Executor getTimeoutExecutorAfter() {
		return timeoutExecutorAfter;
	}

	public void setTimeoutExecutorAfter(Executor executor) {
		timeoutExecutorAfter = executor;
	}

	public void setDebuggerCallback(Consumer<Scope> callback) {
		debuggerCallback = callback;
	}

	public void onDebugger(Scope scope) {
		if (debuggerCallback != null) {
			debuggerCallback.accept(scope);
		}
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public WrappedObject wrap(Scope scope, Object o) {
		if (o == null) {
			return Special.NULL;
		} else if (o instanceof WrappedObjectFactory factory) {
			return factory.create(this, scope);
		} else if (o instanceof Boolean b) {
			return b ? BooleanJS.TRUE : BooleanJS.FALSE;
		} else if (o instanceof Number n) {
			return new NumberJS(n);
		} else if (o instanceof CharSequence) {
			return new StringJS(o.toString());
		} else if (o instanceof PrototypeSupplier proto) {
			return new JavaObjectJS<>(o, proto.getPrototype(this, scope));
		}

		var c = o.getClass();

		if (o instanceof List list) {
			return new ListJS<>(list, listPrototype);
		} else if (o instanceof Collection collection) {
			return new CollectionJS<>(collection, collectionPrototype);
		} else if (o instanceof Iterable iterable) {
			return new IterableJS<>(iterable, iterablePrototype);
		} else if (o instanceof Map map) {
			return new MapJS(map, mapPrototype);
		} else if (c.isArray()) {
			return new ListJS<>(NativeArrayList.of(o), arrayPrototype);
		} else {
			return new JavaObjectJS<>(o, getClassPrototype(c));
		}
	}

	@Override
	public Prototype getPrototype(Scope scope, Object o) {
		if (o == null) {
			return Special.NULL.prototype;
		} else if (o instanceof CharSequence) {
			return StringJS.PROTOTYPE;
		} else if (o instanceof Number) {
			return NumberJS.PROTOTYPE;
		} else if (o instanceof Boolean) {
			return BooleanJS.PROTOTYPE;
		} else if (o instanceof PrototypeSupplier s) {
			return s.getPrototype(this, scope);
		} else if (o instanceof Class) {
			return ClassJS.PROTOTYPE;
		} else {
			return getClassPrototype(o.getClass());
		}
	}
}
