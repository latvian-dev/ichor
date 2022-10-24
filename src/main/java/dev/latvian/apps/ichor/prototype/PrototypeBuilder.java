package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.js.NumberJS;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PrototypeBuilder implements Prototype, Callable {
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
	public Object get(Context cx, String name, @Nullable Object self) {
		if (members != null) {
			var m = members.get(name);

			if (m != null) {
				return m.get(cx, self);
			}
		}

		if (namedValueHandler != null && self != null) {
			return namedValueHandler.get(cx, name, self);
		}

		var parent0 = parent;

		while (parent0 != null) {
			var v = parent0.get(cx, name, self);

			if (v != Special.NOT_FOUND) {
				return v;
			}

			parent0 = parent0.getPrototype();
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Context cx, String name, @Nullable Object self, @Nullable Object value) {
		if (namedValueHandler != null && self != null) {
			return namedValueHandler.set(cx, name, self, value);
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.set(cx, name, self, value)) {
				return true;
			}

			parent0 = parent0.getPrototype();
		}

		return false;
	}

	@Override
	public boolean delete(Context cx, String name, @Nullable Object self) {
		if (namedValueHandler != null && self != null) {
			return namedValueHandler.delete(cx, name, self);
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.delete(cx, name, self)) {
				return true;
			}

			parent0 = parent0.getPrototype();
		}

		return false;
	}

	@Override
	@Nullable
	public Object get(Context cx, int index, @Nullable Object self) {
		if (indexedValueHandler != null && self != null) {
			return indexedValueHandler.get(cx, index, self);
		}

		var parent0 = parent;

		while (parent0 != null) {
			var v = parent0.get(cx, index, self);

			if (v != Special.NOT_FOUND) {
				return v;
			}

			parent0 = parent0.getPrototype();
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Context cx, int index, @Nullable Object self, @Nullable Object value) {
		if (indexedValueHandler != null && self != null) {
			return indexedValueHandler.set(cx, index, self, value);
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.set(cx, index, self, value)) {
				return true;
			}

			parent0 = parent0.getPrototype();
		}

		return false;
	}

	@Override
	public boolean delete(Context cx, int index, @Nullable Object self) {
		if (indexedValueHandler != null && self != null) {
			return indexedValueHandler.delete(cx, index, self);
		}

		var parent0 = parent;

		while (parent0 != null) {
			if (parent0.delete(cx, index, self)) {
				return true;
			}

			parent0 = parent0.getPrototype();
		}

		return false;
	}

	@Override
	public Object construct(Context cx, Object[] args) {
		if (constructor != null) {
			return constructor.construct(cx, args, true);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public Object call(Context cx, Object[] args) {
		if (constructor != null) {
			return constructor.construct(cx, args, false);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public String asString(Context cx, Object self) {
		if (asString != null) {
			return asString.asString(cx, self);
		}

		return self.toString();
	}

	@Override
	public Number asNumber(Context cx, Object self) {
		if (asNumber != null) {
			return asNumber.asNumber(cx, self);
		}

		return NumberJS.ONE;
	}

	@Override
	public Boolean asBoolean(Context cx, Object self) {
		if (asBoolean != null) {
			return asBoolean.asBoolean(cx, self);
		}

		return Boolean.TRUE;
	}
}
