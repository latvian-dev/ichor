package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.js.ArrayJS;
import dev.latvian.apps.ichor.js.BooleanJS;
import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.js.ObjectJS;
import dev.latvian.apps.ichor.js.StringJS;
import dev.latvian.apps.ichor.js.java.JavaClassJS;
import dev.latvian.apps.ichor.js.java.JavaTypePrototype;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;

import java.util.HashMap;
import java.util.Map;

public class Context {
	public final RootScope rootScope;
	private Map<Class<?>, Prototype> classPrototypeCache;
	private Map<String, Object> properties;

	public Context() {
		rootScope = new RootScope(this);
	}

	public void reset() {
		rootScope.deleteAllDeclaredMembers();
	}

	public void addSafeClasses() {
		rootScope.add(StringJS.PROTOTYPE);
		rootScope.add(NumberJS.PROTOTYPE);
		rootScope.add(BooleanJS.PROTOTYPE);
		rootScope.add(ObjectJS.PROTOTYPE);
		rootScope.add(ArrayJS.PROTOTYPE);
	}

	public Object getProperty(String name, Object defaultValue) {
		return properties == null ? defaultValue : properties.getOrDefault(name, defaultValue);
	}

	public void setProperty(String name, Object value) {
		if (properties == null) {
			properties = new HashMap<>(1);
		}

		properties.put(name, value);
	}

	public String asString(Scope scope, Object o) {
		if (o == null) {
			return "null";
		} else if (o instanceof CharSequence || o instanceof Number || o instanceof Boolean || o instanceof Special) {
			return o.toString();
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalString(scope);
		}

		return getPrototype(o).asString(scope, o);
	}

	public Number asNumber(Scope scope, Object o) {
		if (Special.isInvalid(o)) {
			return NumberJS.NaN;
		} else if (o instanceof Number) {
			return (Number) o;
		} else if (o instanceof Boolean) {
			return (Boolean) o ? NumberJS.ONE : NumberJS.ZERO;
		} else if (o instanceof CharSequence) {
			try {
				var d = Double.parseDouble(o.toString());
				return d == 0D ? NumberJS.ZERO : d == 1D ? NumberJS.ONE : d;
			} catch (Exception ex) {
				return NumberJS.NaN;
			}
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalDouble(scope);
		}

		return getPrototype(o).asNumber(scope, o);
	}

	public double asDouble(Scope scope, Object o) {
		if (Special.isInvalid(o)) {
			return Double.NaN;
		} else if (o instanceof Number) {
			return ((Number) o).intValue();
		} else if (o instanceof Boolean) {
			return (Boolean) o ? 1D : 0D;
		} else if (o instanceof CharSequence) {
			try {
				return Double.parseDouble(o.toString());
			} catch (Exception ex) {
				return 0;
			}
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalDouble(scope);
		}

		return asNumber(scope, o).doubleValue();
	}

	public int asInt(Scope scope, Object o) {
		if (Special.isInvalid(o)) {
			return -1;
		} else if (o instanceof Number) {
			return ((Number) o).intValue();
		} else if (o instanceof Boolean) {
			return (Boolean) o ? 1 : 0;
		} else if (o instanceof CharSequence) {
			try {
				return (int) Long.parseLong(o.toString());
			} catch (Exception ex) {
				return 0;
			}
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalInt(scope);
		}

		return asNumber(scope, o).intValue();
	}

	public long asLong(Scope scope, Object o) {
		if (Special.isInvalid(o)) {
			return -1L;
		} else if (o instanceof Number) {
			return ((Number) o).longValue();
		} else if (o instanceof Boolean) {
			return (Boolean) o ? 1L : 0L;
		} else if (o instanceof CharSequence) {
			try {
				return Long.parseLong(o.toString());
			} catch (Exception ex) {
				return 0L;
			}
		} else if (o instanceof Evaluable) {
			// add evalLong
			return ((Evaluable) o).evalInt(scope);
		}

		return asNumber(scope, o).longValue();
	}

	public Boolean asBoolean(Scope scope, Object o) {
		if (o instanceof Boolean) {
			return (Boolean) o;
		} else if (Special.isInvalid(o)) {
			return Boolean.FALSE;
		} else if (o instanceof Number) {
			return ((Number) o).doubleValue() != 0D;
		} else if (o instanceof CharSequence) {
			return o.toString().equalsIgnoreCase("true");
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalBoolean(scope);
		}

		return getPrototype(o).asBoolean(scope, o);
	}

	public char asChar(Scope scope, Object o) {
		if (o instanceof Character) {
			return (Character) o;
		} else if (o instanceof CharSequence) {
			return ((CharSequence) o).charAt(0);
		} else if (o instanceof Number) {
			return (char) ((Number) o).intValue();
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalString(scope).charAt(0);
		}

		return 0;
	}

	@SuppressWarnings("unchecked")
	public <T> T as(Scope scope, Object o, Class<T> toType) {
		if (Special.isInvalid(o)) {
			return null;
		} else if (toType == null || toType == Void.TYPE || toType == Object.class || toType.isInstance(o)) {
			return (T) o;
		} else if (toType == String.class || toType == CharSequence.class) {
			return (T) asString(scope, o);
		} else if (toType == Number.class) {
			return (T) asNumber(scope, o);
		} else if (toType == Boolean.class || toType == Boolean.TYPE) {
			return (T) asBoolean(scope, o);
		} else if (toType == Character.class || toType == Character.TYPE) {
			return (T) Character.valueOf(asChar(scope, o));
		} else if (toType == Byte.class || toType == Byte.TYPE) {
			return (T) Byte.valueOf(asNumber(scope, o).byteValue());
		} else if (toType == Short.class || toType == Short.TYPE) {
			return (T) Short.valueOf(asNumber(scope, o).shortValue());
		} else if (toType == Integer.class || toType == Integer.TYPE) {
			return (T) Integer.valueOf(asNumber(scope, o).intValue());
		} else if (toType == Long.class || toType == Long.TYPE) {
			return (T) Long.valueOf(asNumber(scope, o).longValue());
		} else if (toType == Float.class || toType == Float.TYPE) {
			return (T) Float.valueOf(asNumber(scope, o).floatValue());
		} else if (toType == Double.class || toType == Double.TYPE) {
			return (T) Double.valueOf(asNumber(scope, o).doubleValue());
		}

		throw new ScriptError("Cannot cast " + o.getClass().getName() + " to " + toType.getName());
	}

	public Prototype getPrototype(Object o) {
		if (o == null) {
			return Special.NULL.getPrototype();
		} else if (o instanceof CharSequence) {
			return StringJS.PROTOTYPE;
		} else if (o instanceof Number) {
			return NumberJS.PROTOTYPE;
		} else if (o instanceof Boolean) {
			return BooleanJS.PROTOTYPE;
		} else if (o instanceof PrototypeSupplier s) {
			return s.getPrototype();
		} else if (o instanceof Class) {
			return JavaClassJS.PROTOTYPE;
		} else if (o instanceof Map<?, ?>) {
			return ObjectJS.PROTOTYPE;
		} else if (o instanceof Iterable<?> || o.getClass().isArray()) {
			return ArrayJS.PROTOTYPE;
		}

		return getClassPrototype0(o.getClass());
	}

	public Prototype getClassPrototype(Class<?> c) {
		if (c == null || c == Void.class || c == Void.TYPE) {
			return Special.NULL.getPrototype();
		} else if (c == String.class || c == Character.class || c == Character.TYPE) {
			return StringJS.PROTOTYPE;
		} else if (c == Boolean.class || c == Boolean.TYPE) {
			return BooleanJS.PROTOTYPE;
		} else if (c.isPrimitive()) {
			return NumberJS.PROTOTYPE;
		} else if (c == Class.class) {
			return JavaClassJS.PROTOTYPE;
		} else if (Number.class.isAssignableFrom(c)) {
			return NumberJS.PROTOTYPE;
		} else if (CharSequence.class.isAssignableFrom(c)) {
			return StringJS.PROTOTYPE;
		} else if (Map.class.isAssignableFrom(c)) {
			return ObjectJS.PROTOTYPE;
		} else if (Iterable.class.isAssignableFrom(c) || c.isArray()) {
			return ArrayJS.PROTOTYPE;
		}

		return getClassPrototype0(c);
	}

	private Prototype getClassPrototype0(Class<?> c) {
		if (classPrototypeCache == null) {
			classPrototypeCache = new HashMap<>();
		}

		var p = classPrototypeCache.get(c);

		if (p == null) {
			p = new JavaTypePrototype(this, c);
			classPrototypeCache.put(c, p);
		}

		return p;
	}
}
