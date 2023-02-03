package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.java.BooleanPrototype;
import dev.latvian.apps.ichor.java.JavaClassPrototype;
import dev.latvian.apps.ichor.js.type.ArrayJS;
import dev.latvian.apps.ichor.js.type.CollectionJS;
import dev.latvian.apps.ichor.js.type.IterableJS;
import dev.latvian.apps.ichor.js.type.ListJS;
import dev.latvian.apps.ichor.js.type.MapJS;
import dev.latvian.apps.ichor.js.type.MathJS;
import dev.latvian.apps.ichor.js.type.NumberJS;
import dev.latvian.apps.ichor.js.type.ObjectJS;
import dev.latvian.apps.ichor.js.type.RegExpJS;
import dev.latvian.apps.ichor.js.type.StringJS;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class ContextJS extends Context {
	private Executor timeoutExecutor, timeoutExecutorAfter;
	private Consumer<Scope> debuggerCallback;

	public final Prototype<?> classPrototype,
			stringPrototype,
			numberPrototype,
			booleanPrototype,
			jsObjectPrototype,
			jsArrayPrototype,
			jsMathPrototype,
			jsMapPrototype,
			regExpPrototype,
			listPrototype,
			collectionPrototype,
			iterablePrototype,
			arrayPrototype;

	public ContextJS() {
		timeoutExecutor = null;
		timeoutExecutorAfter = null;

		classPrototype = new JavaClassPrototype(this);
		stringPrototype = new StringJS(this);
		numberPrototype = new NumberJS(this);
		booleanPrototype = new BooleanPrototype(this);
		jsObjectPrototype = new ObjectJS(this);
		jsArrayPrototype = new ArrayJS(this);
		jsMathPrototype = new MathJS(this);
		jsMapPrototype = new MapJS(this);
		regExpPrototype = new RegExpJS(this);
		listPrototype = new ListJS(this);
		collectionPrototype = new CollectionJS(this);
		iterablePrototype = new IterableJS(this);

		arrayPrototype = createJavaPrototype(Object[].class);
	}

	@Override
	public List<Prototype<?>> getSafePrototypes() {
		return List.of(
				stringPrototype,
				numberPrototype,
				booleanPrototype,
				jsObjectPrototype,
				jsArrayPrototype,
				jsMathPrototype,
				jsMapPrototype
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
	public Prototype<?> getPrototype(Scope scope, Object o) {
		if (o == null) {
			return Special.NULL.prototype;
		} else if (o instanceof PrototypeSupplier s) {
			return s.getPrototype(this, scope);
		} else if (o instanceof Boolean) {
			return booleanPrototype;
		} else if (o instanceof Number) {
			return numberPrototype;
		} else if (o instanceof CharSequence) {
			return stringPrototype;
		} else if (o instanceof Class) {
			return classPrototype;
		} else if (o instanceof Pattern) {
			return regExpPrototype;
		} else if (o.getClass().isArray()) {
			return arrayPrototype;
		} else {
			return getClassPrototype(o.getClass());
		}
	}

	@Override
	public Prototype<?> getClassPrototype(Class<?> c) {
		if (c == Boolean.class) {
			return booleanPrototype;
		} else if (c == Number.class) {
			return numberPrototype;
		} else if (c == String.class) {
			return stringPrototype;
		} else if (c == Class.class) {
			return classPrototype;
		} else if (c == Map.class) {
			return jsMapPrototype;
		} else if (c == List.class) {
			return listPrototype;
		} else if (c == Collection.class) {
			return collectionPrototype;
		} else if (c == Iterable.class) {
			return iterablePrototype;
		} else if (c == Pattern.class) {
			return regExpPrototype;
		} else if (c.isArray()) {
			return arrayPrototype;
		} else {
			return super.getClassPrototype(c);
		}
	}
}
