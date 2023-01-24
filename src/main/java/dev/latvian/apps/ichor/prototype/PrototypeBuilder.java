package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.ConstructorError;
import dev.latvian.apps.ichor.js.NumberJS;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrototypeBuilder implements Prototype, Callable {
	private final String prototypeName;
	protected Prototype parent;
	private PrototypeConstructor constructor;
	private Map<String, PrototypeProperty> members;
	private Map<String, PrototypeStaticProperty> staticMembers;
	private PrototypeAsString asString;
	private PrototypeAsNumber asNumber;
	private PrototypeAsBoolean asBoolean;
	private Prototype customMembers;

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

	public PrototypeBuilder property(String name, PrototypeProperty property) {
		if (members == null) {
			members = new HashMap<>(1);
		}

		members.put(name, property);
		return this;
	}

	public PrototypeBuilder function(String name, PrototypeFunction value) {
		return property(name, value);
	}

	public PrototypeBuilder staticProperty(String name, PrototypeStaticProperty property) {
		if (staticMembers == null) {
			staticMembers = new HashMap<>(1);
		}

		staticMembers.put(name, property);
		return this;
	}

	public PrototypeBuilder staticFunction(String name, PrototypeStaticFunction value) {
		return staticProperty(name, value);
	}

	public PrototypeBuilder constant(String name, Object value) {
		return staticProperty(name, new PrototypeConstant(value));
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

	public PrototypeBuilder customMembers(Prototype value) {
		customMembers = value;
		return this;
	}

	// Impl //

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, Object self, String name) {
		initLazy();

		if (self != null && self != this) {
			if (members != null) {
				var m = members.get(name);

				if (m != null) {
					return m.get(cx, scope, self);
				}
			}
		}

		if (staticMembers != null) {
			var m = staticMembers.get(name);

			if (m != null) {
				return m.get(cx, scope);
			}
		}

		if (customMembers != null && self != null) {
			var m = customMembers.get(cx, scope, self, name);

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

		if (self != null && self != this) {
			if (members != null) {
				var m = members.get(name);

				if (m != null) {
					return m.set(cx, scope, self, value);
				}
			}
		}

		if (staticMembers != null) {
			var m = staticMembers.get(name);

			if (m != null) {
				return m.set(cx, scope, value);
			}
		}

		if (customMembers != null && self != null) {
			if (customMembers.set(cx, scope, self, name, value)) {
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

		if (customMembers != null && self != null) {
			if (customMembers.delete(cx, scope, self, name)) {
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
		return customMembers != null ? customMembers.keys(cx, scope, self) : Collections.emptySet();
	}

	@Override
	public Collection<?> values(Context cx, Scope scope, Object self) {
		return customMembers != null ? customMembers.values(cx, scope, self) : Collections.emptySet();
	}

	@Override
	public Collection<?> entries(Context cx, Scope scope, Object self) {
		return customMembers != null ? customMembers.entries(cx, scope, self) : Collections.emptySet();
	}

	@Override
	public int getMemberCount(Context cx, Scope scope, Object self) {
		return customMembers != null ? customMembers.getMemberCount(cx, scope, self) : 0;
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, Object self, int index) {
		initLazy();

		if (customMembers != null && self != null) {
			var m = customMembers.get(cx, scope, self, index);

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

		if (customMembers != null && self != null) {
			if (customMembers.set(cx, scope, self, index, value)) {
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

		if (customMembers != null && self != null) {
			if (customMembers.delete(cx, scope, self, index)) {
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
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		initLazy();

		if (constructor != null) {
			return constructor.construct(cx, scope, args, false);
		}

		throw new ConstructorError(this);
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
