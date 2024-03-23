package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.ScriptTimedOutError;
import dev.latvian.apps.ichor.java.AnnotatedElementPrototype;
import dev.latvian.apps.ichor.java.BooleanPrototype;
import dev.latvian.apps.ichor.java.JavaClassPrototype;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.slot.Slot;
import dev.latvian.apps.ichor.type.ArrayJS;
import dev.latvian.apps.ichor.type.CollectionJS;
import dev.latvian.apps.ichor.type.IterableJS;
import dev.latvian.apps.ichor.type.ListJS;
import dev.latvian.apps.ichor.type.MapJS;
import dev.latvian.apps.ichor.type.MathJS;
import dev.latvian.apps.ichor.type.NumberJS;
import dev.latvian.apps.ichor.type.ObjectJS;
import dev.latvian.apps.ichor.type.RegExpJS;
import dev.latvian.apps.ichor.type.SetJS;
import dev.latvian.apps.ichor.type.StringJS;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class RootScope extends Scope {
	public final Context context;
	public int maxScopeDepth;
	public long interpretingTimeout;
	protected long timeoutAt;
	final Map<Class<?>, Prototype<?>> classPrototypes;

	public final Prototype<?> objectPrototype,
			arrayPrototype,
			classPrototype,
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
			iterablePrototype;

	public final List<Prototype<?>> safePrototypes;

	public RootScope(Context cx) {
		super(null);
		context = cx;
		root = this;
		scopeOwner = cx;
		maxScopeDepth = cx.getMaxScopeDepth();
		interpretingTimeout = cx.getInterpretingTimeout();
		classPrototypes = new IdentityHashMap<>();

		objectPrototype = new Prototype<>(this, Object.class);
		arrayPrototype = new Prototype<>(this, Object[].class);
		classPrototype = new Prototype<>(this, Class.class);
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

		registerPrototype(new JavaClassPrototype(this));
		registerPrototype(new AnnotatedElementPrototype(this));
	}

	public List<Prototype<?>> getSafePrototypes() {
		return safePrototypes;
	}

	public void registerPrototype(Prototype<?> prototype) {
		classPrototypes.put(prototype.type, prototype);
	}

	public void addRoot(String name, @Nullable Object value) {
		add(name, value, (byte) (Slot.IMMUTABLE | Slot.ROOT));
	}

	public void addSafePrototypes() {
		for (var p : getSafePrototypes()) {
			addRoot(p.getPrototypeName(), p);
		}
	}

	@Override
	public String toString() {
		return "RootScope";
	}

	public void checkTimeout() {
		if (timeoutAt > 0L && System.currentTimeMillis() >= timeoutAt) {
			throw new ScriptTimedOutError();
		}
	}

	public void interpret(Interpretable interpretable) {
		timeoutAt = interpretingTimeout > 0L ? System.currentTimeMillis() + interpretingTimeout : 0L;

		try {
			interpretable.interpret(this);
		} finally {
			timeoutAt = 0L;
		}
	}
}
