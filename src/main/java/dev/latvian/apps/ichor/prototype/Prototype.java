package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Ref;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.IchorError;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Prototype implements PrototypeSupplier, Ref {
	public final String name;
	private PrototypeConstructor constructor;
	private Map<String, PrototypeProperty> properties;
	private Map<String, PrototypeProperty> staticProperties;
	private Map<String, PrototypeFunction> functions;
	private Map<String, PrototypeFunction> staticFunctions;
	private PrototypeToString toString;
	private PrototypeToNumber toNumber;
	private PrototypeToBoolean toBoolean;

	public Prototype(String n) {
		name = n;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public final Prototype getPrototype() {
		return this;
	}

	// Builder //

	public Prototype constructor(PrototypeConstructor c) {
		constructor = c;
		return this;
	}

	public Prototype property(String name, PrototypeProperty value) {
		if (properties == null) {
			properties = new HashMap<>(1);
		}

		properties.put(name, value);
		return this;
	}

	public Prototype staticProperty(String name, StaticPrototypeProperty value) {
		if (staticProperties == null) {
			staticProperties = new HashMap<>(1);
		}

		staticProperties.put(name, value);
		return this;
	}

	public Prototype function(String name, PrototypeFunction value) {
		if (functions == null) {
			functions = new HashMap<>(1);
		}

		functions.put(name, value);
		return this;
	}

	public Prototype staticFunction(String name, StaticPrototypeFunction value) {
		if (staticFunctions == null) {
			staticFunctions = new HashMap<>(1);
		}

		staticFunctions.put(name, value);
		return this;
	}

	public Prototype toString(PrototypeToString value) {
		toString = value;
		return this;
	}

	public Prototype toNumber(PrototypeToNumber value) {
		toNumber = value;
		return this;
	}

	public Prototype toBoolean(PrototypeToBoolean value) {
		toBoolean = value;
		return this;
	}

	// Methods //

	public Object construct(Context cx, Scope scope, Object[] args, boolean hasNew) {
		if (constructor != null) {
			return constructor.construct(cx, scope, args, hasNew);
		}

		throw new IchorError("No constructor for " + name);
	}

	@Nullable
	public PrototypeProperty getProperty(String name, boolean isStatic) {
		if (isStatic) {
			return staticProperties == null ? null : staticProperties.get(name);
		}

		return properties == null ? null : properties.get(name);
	}

	@Nullable
	public PrototypeFunction getFunction(String name, boolean isStatic) {
		if (isStatic) {
			return staticFunctions == null ? null : staticFunctions.get(name);
		}

		return functions == null ? null : functions.get(name);
	}

	public String toString(Context cx, Scope scope, Object self) {
		if (toString == null) {
			toString = PrototypeToString.DEFAULT;
		}

		return toString.toString(cx, scope, self);
	}

	public double toNumber(Context cx, Scope scope, Object self) {
		if (toNumber == null) {
			toNumber = PrototypeToNumber.DEFAULT;
		}

		return toNumber.toNumber(cx, scope, self);
	}

	public boolean toBoolean(Context cx, Scope scope, Object self) {
		if (toBoolean == null) {
			toBoolean = PrototypeToBoolean.DEFAULT;
		}

		return toBoolean.toBoolean(cx, scope, self);
	}
}
