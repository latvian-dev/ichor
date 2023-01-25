package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.WrappedObject;
import dev.latvian.apps.ichor.WrappedObjectFactory;
import dev.latvian.apps.ichor.java.ClassJS;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class ContextJS extends Context {
	private Executor timeoutExecutor, timeoutExecutorAfter;
	private Consumer<Scope> debuggerCallback;

	public ContextJS() {
		timeoutExecutor = null;
		timeoutExecutorAfter = null;
	}

	@Override
	public List<Prototype> getSafePrototypes() {
		return List.of(
				StringJS.PROTOTYPE,
				NumberJS.PROTOTYPE,
				BooleanJS.PROTOTYPE,
				ArrayJS.PROTOTYPE,
				MapJS.PROTOTYPE
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
		}

		var c = o.getClass();

		if (o instanceof List list) {
			return new ListJS<>(list, getClassPrototype0(c));
		} else if (o instanceof Collection collection) {
			return new CollectionJS<>(collection, getClassPrototype0(c));
		} else if (o instanceof Iterable iterable) {
			return new IterableJS<>(iterable, getClassPrototype0(c));
		} else if (o instanceof Map map) {
			return new MapJS(map, getClassPrototype0(c));
		} else if (c == Object[].class) {
			return new ListJS<>(Arrays.asList((Object[]) o), getClassPrototype0(Object[].class));
		} else if (c.isArray()) {
			return new ArrayJS(o);
		} else {
			return new JavaObjectJS<>(o, getClassPrototype0(c));
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
			return getClassPrototype0(o.getClass());
		}
	}

	@Override
	public Prototype getClassPrototype(Class<?> c) {
		if (c == null || c == Void.class || c == Void.TYPE) {
			return Special.NULL.prototype;
		} else if (c == String.class || c == Character.class || c == Character.TYPE) {
			return StringJS.PROTOTYPE;
		} else if (c == Boolean.class || c == Boolean.TYPE) {
			return BooleanJS.PROTOTYPE;
		} else if (c.isPrimitive()) {
			return NumberJS.PROTOTYPE;
		} else if (c == Class.class) {
			return ClassJS.PROTOTYPE;
		} else if (Number.class.isAssignableFrom(c)) {
			return NumberJS.PROTOTYPE;
		} else if (CharSequence.class.isAssignableFrom(c)) {
			return StringJS.PROTOTYPE;
		} else if (c.isArray()) {
			return ArrayJS.PROTOTYPE;
		} else {
			return getClassPrototype0(c);
		}
	}
}
