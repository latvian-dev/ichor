package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.js.NumberJS;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrototypeBuilder implements Prototype {
	private final String prototypeName;
	protected Prototype parent;
	private PrototypeConstructor constructor;
	private Map<String, PrototypeProperty> members;
	private PrototypeAsString asString;
	private PrototypeAsNumber asNumber;
	private PrototypeAsBoolean asBoolean;
	private Prototype namedValueHandler;
	private Prototype indexedValueHandler;

	public PrototypeBuilder(String n) {
		prototypeName = n;
	}

	@Override
	public String getPrototypeName() {
		return prototypeName;
	}

	@Override
	public String toString() {
		return prototypeName;
	}

	protected void initLazy() {
	}

	// Builder //

	public PrototypeBuilder constructor(PrototypeConstructor c) {
		constructor = c;
		return this;
	}

	private PrototypeBuilder member(String name, PrototypeProperty member) {
		if (members == null) {
			members = new HashMap<>(1);
		}

		members.put(name, member);
		return this;
	}

	public PrototypeBuilder property(String name, PrototypeProperty value) {
		return member(name, value);
	}

	public PrototypeBuilder staticProperty(String name, PrototypeStaticProperty value) {
		return member(name, value);
	}

	public PrototypeBuilder constant(String name, Object value) {
		return member(name, new PrototypeConstant(value));
	}

	public PrototypeBuilder function(String name, PrototypeFunction value) {
		return member(name, value);
	}

	public PrototypeBuilder staticFunction(String name, PrototypeStaticFunction value) {
		return member(name, value);
	}

	public PrototypeBuilder asString(PrototypeAsString value) {
		asString = value;
		return this;
	}

	public PrototypeBuilder asNumber(PrototypeAsNumber value) {
		asNumber = value;
		return this;
	}

	public PrototypeBuilder asBoolean(PrototypeAsBoolean value) {
		asBoolean = value;
		return this;
	}

	public PrototypeBuilder namedValueHandler(Prototype value) {
		namedValueHandler = value;
		return this;
	}

	public PrototypeBuilder indexedValueHandler(Prototype value) {
		indexedValueHandler = value;
		return this;
	}

	// Impl //

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, Object self, String name) {
		initLazy();

		if (members != null) {
			var m = members.get(name);

			if (m != null) {
				return m.get(cx, scope, self);
			}
		}

		if (namedValueHandler != null && self != null) {
			var m = namedValueHandler.get(cx, scope, self, name);

			if (m != Special.NOT_FOUND) {
				return m;
			}
		}

		var parent0 = parent;

		while (parent0 != null) {
			var v = parent0.get(cx, scope, self, name);

			if (v != Special.NOT_FOUND) {
				return v;
			}

			parent0 = parent0.getPrototype(cx, scope);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Context cx, Scope scope, Object self, String name, @Nullable Object value) {
		initLazy();

		if (members != null) {
			var m = members.get(name);

			if (m != null) {
				return m.set(cx, scope, self, value);
			}
		}

		if (namedValueHandler != null && self != null) {
			if (namedValueHandler.set(cx, scope, self, name, value)) {
				return true;
			}
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.set(cx, scope, self, name, value)) {
				return true;
			}

			parent0 = parent0.getPrototype(cx, scope);
		}

		return false;
	}

	@Override
	public boolean delete(Context cx, Scope scope, Object self, String name) {
		initLazy();

		if (namedValueHandler != null && self != null) {
			if (namedValueHandler.delete(cx, scope, self, name)) {
				return true;
			}
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.delete(cx, scope, self, name)) {
				return true;
			}

			parent0 = parent0.getPrototype(cx, scope);
		}

		return false;
	}

	@Override
	public Collection<?> keys(Context cx, Scope scope, Object self) {
		var h = namedValueHandler != null ? namedValueHandler : indexedValueHandler;
		return h != null ? h.keys(cx, scope, self) : Collections.emptySet();
	}

	@Override
	public Collection<?> values(Context cx, Scope scope, Object self) {
		var h = namedValueHandler != null ? namedValueHandler : indexedValueHandler;
		return h != null ? h.values(cx, scope, self) : Collections.emptySet();
	}

	@Override
	public Collection<?> entries(Context cx, Scope scope, Object self) {
		var h = namedValueHandler != null ? namedValueHandler : indexedValueHandler;
		return h != null ? h.entries(cx, scope, self) : Collections.emptySet();
	}

	@Override
	public int getMemberCount(Context cx, Scope scope, Object self) {
		var h = namedValueHandler != null ? namedValueHandler : indexedValueHandler;
		return h != null ? h.getMemberCount(cx, scope, self) : 0;
	}

	@Override
	public boolean isArrayPrototype() {
		return indexedValueHandler != null;
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, Object self, int index) {
		initLazy();

		if (indexedValueHandler != null && self != null) {
			var m = indexedValueHandler.get(cx, scope, self, index);

			if (m != Special.NOT_FOUND) {
				return m;
			}
		}

		var parent0 = parent;

		while (parent0 != null) {
			var v = parent0.get(cx, scope, self, index);

			if (v != Special.NOT_FOUND) {
				return v;
			}

			parent0 = parent0.getPrototype(cx, scope);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Context cx, Scope scope, Object self, int index, @Nullable Object value) {
		initLazy();

		if (indexedValueHandler != null && self != null) {
			if (indexedValueHandler.set(cx, scope, self, index, value)) {
				return true;
			}
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.set(cx, scope, self, index, value)) {
				return true;
			}

			parent0 = parent0.getPrototype(cx, scope);
		}

		return false;
	}

	@Override
	public boolean delete(Context cx, Scope scope, Object self, int index) {
		initLazy();

		if (indexedValueHandler != null && self != null) {
			if (indexedValueHandler.delete(cx, scope, self, index)) {
				return true;
			}
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.delete(cx, scope, self, index)) {
				return true;
			}

			parent0 = parent0.getPrototype(cx, scope);
		}

		return false;
	}

	@Override
	public Object call(Context cx, Scope scope, Object[] args) {
		initLazy();

		if (constructor != null) {
			return constructor.construct(cx, scope, args, false);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public void asString(Context cx, Scope scope, Object self, StringBuilder builder, boolean escape) {
		if (asString != null) {
			asString.asString(cx, scope, self, builder, escape);
		} else {
			builder.append(self);
		}
	}

	@Override
	public Number asNumber(Context cx, Scope scope, Object self) {
		if (asNumber != null) {
			return asNumber.asNumber(cx, scope, self);
		}

		return NumberJS.ONE;
	}

	@Override
	public boolean asBoolean(Context cx, Scope scope, Object self) {
		if (asBoolean != null) {
			return asBoolean.asBoolean(cx, scope, self);
		}

		return true;
	}
}
