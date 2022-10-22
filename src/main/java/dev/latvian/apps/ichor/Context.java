package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.IchorError;
import dev.latvian.apps.ichor.js.BooleanJS;
import dev.latvian.apps.ichor.js.JavaClassJS;
import dev.latvian.apps.ichor.js.JavaTypePrototype;
import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.js.StringJS;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import dev.latvian.apps.ichor.util.AssignType;
import dev.latvian.apps.ichor.util.RootScope;

import java.util.HashMap;
import java.util.Map;

public class Context {
	public final RootScope rootScope;
	private Map<Class<?>, Prototype> classPrototypeCache;

	public Context() {
		rootScope = new RootScope(this);
	}

	public void reset() {
		rootScope.deleteAllDeclaredMembers();
	}

	public void addSafeClasses() {
		rootScope.declareMember("String", StringJS.PROTOTYPE, AssignType.IMMUTABLE);
		rootScope.declareMember("Number", NumberJS.PROTOTYPE, AssignType.IMMUTABLE);
		rootScope.declareMember("Boolean", BooleanJS.PROTOTYPE, AssignType.IMMUTABLE);
	}

	public String toString(Scope scope, Object o) {
		if (o == null) {
			return "null";
		} else if (o instanceof CharSequence || o instanceof Number || o instanceof Boolean) {
			return o.toString();
		}

		return getPrototype(o).toString(this, scope, o);
	}

	public double toNumber(Object o) {
		return 0D;
	}

	public boolean toBoolean(Object o) {
		if (o instanceof Boolean b) {
			return b;
		} else if (o instanceof Number n) {
			return n.doubleValue() != 0D;
		}

		return o != null && o != Special.NULL && o != Special.UNDEFINED;
	}

	public Object cast(Object o, Class<?> toType) {
		if (o == null || o == Special.UNDEFINED || o == Special.NULL) {
			return null;
		} else if (toType.isAssignableFrom(o.getClass())) {
			return o;
		}

		throw new IchorError("Cannot cast " + o.getClass().getName() + " to " + toType.getName());
	}

	public Prototype getPrototype(Object o) {
		if (o instanceof CharSequence) {
			return StringJS.PROTOTYPE;
		} else if (o instanceof Number) {
			return NumberJS.PROTOTYPE;
		} else if (o instanceof Boolean) {
			return BooleanJS.PROTOTYPE;
		} else if (o instanceof PrototypeSupplier s) {
			return s.getPrototype();
		} else if (o instanceof Class) {
			return JavaClassJS.PROTOTYPE;
		}

		return o == null ? Special.NULL : getClassPrototype0(o.getClass());
	}

	public Prototype getClassPrototype(Class<?> c) {
		if (c == null || c == Void.class || c == Void.TYPE) {
			return Special.UNDEFINED;
		} else if (c == Character.class || c == Character.TYPE || CharSequence.class.isAssignableFrom(c)) {
			return StringJS.PROTOTYPE;
		} else if (c == Boolean.class || c == Boolean.TYPE) {
			return BooleanJS.PROTOTYPE;
		} else if (c.isPrimitive() || Number.class.isAssignableFrom(c)) {
			return NumberJS.PROTOTYPE;
		} else if (c == Class.class) {
			return JavaClassJS.PROTOTYPE;
		}

		return getClassPrototype0(c);
	}

	private Prototype getClassPrototype0(Class<?> c) {
		if (classPrototypeCache == null) {
			classPrototypeCache = new HashMap<>();
		}

		var p = classPrototypeCache.get(c);

		if (p == null) {
			p = new JavaTypePrototype(c);
			classPrototypeCache.put(c, p);
		}

		return p;
	}
}
