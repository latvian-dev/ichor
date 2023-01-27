package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.WrappedObject;
import dev.latvian.apps.ichor.error.ConstructorError;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PrototypeBuilder implements Prototype, WrappedObject, Callable {
	private final String prototypeName;
	private PrototypeConstructor constructor;
	private Map<String, PrototypeProperty> members;
	private Map<String, PrototypeStaticProperty> staticMembers;
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

	public PrototypeBuilder customMembers(Prototype value) {
		customMembers = value;
		return this;
	}

	// Impl //


	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		if (staticMembers != null) {
			var m = staticMembers.get(name);

			if (m != null) {
				return m.get(cx, scope);
			}
		}

		return Special.NOT_FOUND;
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, Object self, String name) {
		if (self != null && self != this) {
			if (members != null) {
				var m = members.get(name);

				if (m != null) {
					return m.get(cx, scope, self);
				}
			}
		}

		if (customMembers != null && self != null) {
			return customMembers.get(cx, scope, self, name);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Context cx, Scope scope, String name, @Nullable Object value) {
		if (staticMembers != null) {
			var m = staticMembers.get(name);

			if (m != null) {
				return m.set(cx, scope, value);
			}
		}

		return false;
	}

	@Override
	public boolean set(Context cx, Scope scope, Object self, String name, @Nullable Object value) {
		if (self != null && self != this) {
			if (members != null) {
				var m = members.get(name);

				if (m != null) {
					return m.set(cx, scope, self, value);
				}
			}
		}

		if (customMembers != null && self != null) {
			return customMembers.set(cx, scope, self, name, value);
		}

		return false;
	}

	@Override
	public boolean delete(Context cx, Scope scope, Object self, String name) {
		if (customMembers != null && self != null) {
			return customMembers.delete(cx, scope, self, name);
		}

		return false;
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, Object self, int index) {
		if (customMembers != null && self != null) {
			return customMembers.get(cx, scope, self, index);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Context cx, Scope scope, Object self, int index, @Nullable Object value) {
		if (customMembers != null && self != null) {
			return customMembers.set(cx, scope, self, index, value);
		}

		return false;
	}

	@Override
	public boolean delete(Context cx, Scope scope, Object self, int index) {
		if (customMembers != null && self != null) {
			return customMembers.delete(cx, scope, self, index);
		}

		return false;
	}

	@Override
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		if (constructor != null) {
			return constructor.construct(cx, scope, args, false);
		}

		throw new ConstructorError(this);
	}
}
