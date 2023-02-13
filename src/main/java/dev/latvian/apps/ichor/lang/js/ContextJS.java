package dev.latvian.apps.ichor.lang.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.java.BooleanPrototype;
import dev.latvian.apps.ichor.lang.js.type.ArrayJS;
import dev.latvian.apps.ichor.lang.js.type.CollectionJS;
import dev.latvian.apps.ichor.lang.js.type.IterableJS;
import dev.latvian.apps.ichor.lang.js.type.ListJS;
import dev.latvian.apps.ichor.lang.js.type.MapJS;
import dev.latvian.apps.ichor.lang.js.type.MathJS;
import dev.latvian.apps.ichor.lang.js.type.NumberJS;
import dev.latvian.apps.ichor.lang.js.type.ObjectJS;
import dev.latvian.apps.ichor.lang.js.type.RegExpJS;
import dev.latvian.apps.ichor.lang.js.type.SetJS;
import dev.latvian.apps.ichor.lang.js.type.StringJS;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
			jsSetPrototype,
			regExpPrototype,
			listPrototype,
			collectionPrototype,
			iterablePrototype,
			arrayPrototype;

	public final List<Prototype<?>> safePrototypes;

	public ContextJS() {
		super();
		timeoutExecutor = null;
		timeoutExecutorAfter = null;

		classPrototype = super.getClassPrototype(Class.class);
		stringPrototype = new StringJS(this);
		numberPrototype = new NumberJS(this);
		booleanPrototype = new BooleanPrototype(this);
		jsObjectPrototype = new ObjectJS(this);
		jsArrayPrototype = new ArrayJS(this);
		jsMathPrototype = new MathJS(this);
		jsMapPrototype = new MapJS(this);
		jsSetPrototype = new SetJS(this);
		regExpPrototype = new RegExpJS(this);
		listPrototype = new ListJS(this);
		collectionPrototype = new CollectionJS(this);
		iterablePrototype = new IterableJS(this);

		arrayPrototype = super.getClassPrototype(Object[].class);

		safePrototypes = new ArrayList<>();
		safePrototypes.add(stringPrototype);
		safePrototypes.add(numberPrototype);
		safePrototypes.add(booleanPrototype);
		safePrototypes.add(jsObjectPrototype);
		safePrototypes.add(jsArrayPrototype);
		safePrototypes.add(jsMathPrototype);
		safePrototypes.add(jsMapPrototype);
		safePrototypes.add(jsSetPrototype);
		safePrototypes.add(regExpPrototype);
	}

	@Override
	public List<Prototype<?>> getSafePrototypes() {
		return safePrototypes;
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
		} else if (c == Map.class || c == HashMap.class || c == LinkedHashMap.class || c == IdentityHashMap.class || c == EnumMap.class) {
			return jsMapPrototype;
		} else if (c == Set.class || c == HashSet.class || c == LinkedHashSet.class || c == EnumSet.class) {
			return jsSetPrototype;
		} else if (c == List.class || c == ArrayList.class || c == LinkedList.class) {
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
