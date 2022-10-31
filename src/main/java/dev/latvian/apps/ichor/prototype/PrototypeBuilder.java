package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.js.NumberJS;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PrototypeBuilder implements Prototype {
	public static PrototypeBuilder create(String name) {
		return new PrototypeBuilder(name);
	}

	private final String prototypeName;
	protected Prototype parent;
	private PrototypeConstructor constructor;
	private Map<String, PrototypeMember> members;
	private PrototypeAsString asString;
	private PrototypeAsNumber asNumber;
	private PrototypeAsBoolean asBoolean;
	private PrototypeNamedValueHandler namedValueHandler;
	private PrototypeIndexedValueHandler indexedValueHandler;

	protected PrototypeBuilder(String n) {
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

	public PrototypeBuilder member(String name, PrototypeMember member) {
		if (members == null) {
			members = new HashMap<>(1);
		}

		members.put(name, member);
		return this;
	}

	public PrototypeBuilder property(String name, PrototypeProperty value) {
		return member(name, value);
	}

	public PrototypeBuilder constant(String name, Object value) {
		return member(name, new PrototypeConstant(value));
	}

	public PrototypeBuilder function(String name, PrototypeFunction value) {
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

	public PrototypeBuilder namedValueHandler(PrototypeNamedValueHandler value) {
		namedValueHandler = value;
		return this;
	}

	public PrototypeBuilder indexedValueHandler(PrototypeIndexedValueHandler value) {
		indexedValueHandler = value;
		return this;
	}

	// Impl //

	@Override
	@Nullable
	public Object get(Scope scope, String name, @Nullable Object self) {
		if (members != null) {
			var m = members.get(name);

			if (m != null) {
				return m.get(scope, self);
			}
		}

		if (namedValueHandler != null && self != null) {
			return namedValueHandler.get(scope, name, self);
		}

		var parent0 = parent;

		while (parent0 != null) {
			var v = parent0.get(scope, name, self);

			if (v != Special.NOT_FOUND) {
				return v;
			}

			parent0 = parent0.getPrototype();
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Scope scope, String name, @Nullable Object self, @Nullable Object value) {
		if (namedValueHandler != null && self != null) {
			return namedValueHandler.set(scope, name, self, value);
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.set(scope, name, self, value)) {
				return true;
			}

			parent0 = parent0.getPrototype();
		}

		return false;
	}

	@Override
	public boolean delete(Scope scope, String name, @Nullable Object self) {
		if (namedValueHandler != null && self != null) {
			return namedValueHandler.delete(scope, name, self);
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.delete(scope, name, self)) {
				return true;
			}

			parent0 = parent0.getPrototype();
		}

		return false;
	}

	@Override
	public boolean isArrayPrototype() {
		return indexedValueHandler != null;
	}

	@Override
	@Nullable
	public Object get(Scope scope, int index, @Nullable Object self) {
		if (indexedValueHandler != null && self != null) {
			return indexedValueHandler.get(scope, index, self);
		}

		var parent0 = parent;

		while (parent0 != null) {
			var v = parent0.get(scope, index, self);

			if (v != Special.NOT_FOUND) {
				return v;
			}

			parent0 = parent0.getPrototype();
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Scope scope, int index, @Nullable Object self, @Nullable Object value) {
		if (indexedValueHandler != null && self != null) {
			return indexedValueHandler.set(scope, index, self, value);
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.set(scope, index, self, value)) {
				return true;
			}

			parent0 = parent0.getPrototype();
		}

		return false;
	}

	@Override
	public boolean delete(Scope scope, int index, @Nullable Object self) {
		if (indexedValueHandler != null && self != null) {
			return indexedValueHandler.delete(scope, index, self);
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.delete(scope, index, self)) {
				return true;
			}

			parent0 = parent0.getPrototype();
		}

		return false;
	}

	@Override
	public Object construct(Scope scope, Object[] args, @Nullable Object self) {
		if (constructor != null) {
			return constructor.construct(scope, args, true);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public Object call(Scope scope, Object[] args, @Nullable Object self) {
		if (constructor != null) {
			return constructor.construct(scope, args, false);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public String asString(Scope scope, Object self) {
		if (asString != null) {
			return asString.asString(scope, self);
		}

		return self.toString();
	}

	@Override
	public Number asNumber(Scope scope, Object self) {
		if (asNumber != null) {
			return asNumber.asNumber(scope, self);
		}

		return NumberJS.ONE;
	}

	@Override
	public Boolean asBoolean(Scope scope, Object self) {
		if (asBoolean != null) {
			return asBoolean.asBoolean(scope, self);
		}

		return Boolean.TRUE;
	}
}
