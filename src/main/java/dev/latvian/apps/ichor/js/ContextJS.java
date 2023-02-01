package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.WrappedObject;
import dev.latvian.apps.ichor.WrappedObjectFactory;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class ContextJS extends Context {
	private Executor timeoutExecutor, timeoutExecutorAfter;
	private Consumer<Scope> debuggerCallback;

	public final Prototype classPrototype;
	public final Prototype stringPrototype;
	public final Prototype numberPrototype;
	public final Prototype booleanPrototype;
	public final Prototype jsObjectPrototype;
	public final Prototype jsArrayPrototype;
	public final Prototype jsMathPrototype;

	public final Prototype listPrototype;
	public final Prototype collectionPrototype;
	public final Prototype iterablePrototype;
	public final Prototype mapPrototype;
	public final Prototype arrayPrototype;

	public ContextJS() {
		timeoutExecutor = null;
		timeoutExecutorAfter = null;

		classPrototype = ClassJS.createDefaultPrototype();
		stringPrototype = StringJS.createDefaultPrototype();
		numberPrototype = NumberJS.createDefaultPrototype();
		booleanPrototype = BooleanJS.createDefaultPrototype();
		jsObjectPrototype = MapJS.createDefaultObjectPrototype();
		jsArrayPrototype = ListJS.createDefaultArrayPrototype();
		jsMathPrototype = MathJS.createDefaultPrototype();

		listPrototype = getClassPrototype(List.class);
		collectionPrototype = getClassPrototype(Collection.class);
		iterablePrototype = getClassPrototype(Iterable.class);
		mapPrototype = getClassPrototype(Map.class);
		arrayPrototype = getClassPrototype(Object[].class);
	}

	@Override
	public List<Prototype> getSafePrototypes() {
		return List.of(
				stringPrototype,
				numberPrototype,
				booleanPrototype,
				jsObjectPrototype,
				jsArrayPrototype,
				jsMathPrototype
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
		} else if (o instanceof Map map) {
			return new MapJS(map, mapPrototype);
		} else if (o instanceof List list) {
			return new ListJS<>(list, listPrototype);
		} else if (o instanceof Collection collection) {
			return new CollectionJS<>(collection, collectionPrototype);
		} else if (o instanceof Iterable iterable) {
			return new IterableJS<>(iterable, iterablePrototype);
		} else if (o instanceof Object[] array) {
			return new IndexedArrayJS(array, arrayPrototype);
		}

		var c = o.getClass();

		if (c.isArray()) {
			return new ArrayJS(o, arrayPrototype);
		} else if (c == Class.class) {
			return new ClassJS((Class<?>) o);
		} else {
			return new JavaObjectJS<>(o, getClassPrototype(c));
		}
	}

	@Override
	public Prototype getPrototype(Scope scope, Object o) {
		if (o == null) {
			return Special.NULL.prototype;
		} else if (o instanceof Boolean) {
			return booleanPrototype;
		} else if (o instanceof Number) {
			return numberPrototype;
		} else if (o instanceof CharSequence) {
			return stringPrototype;
		} else if (o instanceof PrototypeSupplier s) {
			return s.getPrototype(this, scope);
		} else if (o instanceof Class) {
			return classPrototype;
		} else if (o instanceof Map) {
			return mapPrototype;
		} else if (o instanceof List) {
			return listPrototype;
		} else if (o instanceof Collection) {
			return collectionPrototype;
		} else if (o instanceof Iterable) {
			return iterablePrototype;
		} else if (o.getClass().isArray()) {
			return arrayPrototype;
		} else {
			return getClassPrototype(o.getClass());
		}
	}

	@Override
	public Prototype getClassPrototype(Class<?> c) {
		if (c == Boolean.class) {
			return booleanPrototype;
		} else if (Number.class.isAssignableFrom(c)) {
			return numberPrototype;
		} else if (CharSequence.class.isAssignableFrom(c)) {
			return stringPrototype;
		} else if (c == Class.class) {
			return classPrototype;
		} else if (Map.class.isAssignableFrom(c)) {
			return mapPrototype;
		} else if (List.class.isAssignableFrom(c)) {
			return listPrototype;
		} else if (Collection.class.isAssignableFrom(c)) {
			return collectionPrototype;
		} else if (Iterable.class.isAssignableFrom(c)) {
			return iterablePrototype;
		} else if (c.isArray()) {
			return arrayPrototype;
		}

		return super.getClassPrototype(c);
	}
}
