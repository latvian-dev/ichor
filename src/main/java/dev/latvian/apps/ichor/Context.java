package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.java.JavaClassPrototype;
import dev.latvian.apps.ichor.java.JavaObjectPrototype;
import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public abstract class Context {
	public static final ContextProperty<Integer> MAX_SCOPE_DEPTH = new ContextProperty<>("maxScopeDepth", 1000);
	public static final ContextProperty<Long> INTERPRETING_TIMEOUT = new ContextProperty<>("interpretingTimeout", 30000L);
	public static final ContextProperty<Long> TOKEN_STREAM_TIMEOUT = new ContextProperty<>("tokenStreamTimeout", 5000L);

	public Debugger debugger;
	public final List<Prototype> safePrototypes;
	public Prototype stringPrototype;
	public Prototype numberPrototype;
	public Prototype booleanPrototype;
	public Prototype listPrototype;
	public Prototype mapPrototype;
	public Prototype classPrototype;
	private Map<Class<?>, Prototype> classPrototypeCache;
	private final Map<ContextProperty<?>, Object> properties;

	public Context() {
		debugger = Debugger.DEFAULT;
		safePrototypes = new ArrayList<>();
		classPrototype = JavaClassPrototype.PROTOTYPE;
		properties = new IdentityHashMap<>();
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperty(ContextProperty<T> property) {
		var val = properties == null ? null : properties.get(property);
		return val == null ? property.defaultValue() : (T) val;
	}

	public <T> void setProperty(ContextProperty<T> property, T value) {
		properties.put(property, value);
	}

	public void asString(Scope scope, Object o, StringBuilder builder) {
		if (o == null) {
			builder.append("null");
		} else if (o instanceof Number) {
			AstStringBuilder.wrapNumber(o, builder);
		} else if (o instanceof CharSequence || o instanceof Character || o instanceof Boolean || o instanceof Special) {
			builder.append(o);
		} else if (o instanceof Evaluable eval) {
			eval.evalString(scope, builder);
		} else {
			getPrototype(o).asString(scope, o, builder);
		}
	}

	public String asString(Scope scope, Object o) {
		var builder = new StringBuilder();
		asString(scope, o, builder);
		return builder.toString();
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
			return ((Number) o).doubleValue();
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
			return 0;
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
			return 0L;
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
			return !o.toString().isEmpty();
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
			var builder = new StringBuilder();
			((Evaluable) o).evalString(scope, builder);
			return builder.charAt(0);
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
		} else if (o instanceof Adaptable adaptable) {
			return adaptable.adapt(this, toType);
		}

		throw new ScriptError("Cannot cast " + o.getClass().getName() + " to " + toType.getName());
	}

	public Prototype getPrototype(Object o) {
		if (o == null) {
			return Special.NULL.getPrototype(this);
		} else if (o instanceof CharSequence) {
			return stringPrototype;
		} else if (o instanceof Number) {
			return numberPrototype;
		} else if (o instanceof Boolean) {
			return booleanPrototype;
		} else if (o instanceof PrototypeSupplier s) {
			return s.getPrototype(this);
		} else if (o instanceof Class) {
			return classPrototype;
		} else if (o instanceof Map<?, ?>) {
			return mapPrototype;
		} else if (o instanceof Iterable<?> || o.getClass().isArray()) {
			return listPrototype;
		}

		return getClassPrototype0(o.getClass());
	}

	public Prototype getClassPrototype(Class<?> c) {
		if (c == null || c == Void.class || c == Void.TYPE) {
			return Special.NULL.getPrototype(this);
		} else if (c == String.class || c == Character.class || c == Character.TYPE) {
			return stringPrototype;
		} else if (c == Boolean.class || c == Boolean.TYPE) {
			return booleanPrototype;
		} else if (c.isPrimitive()) {
			return numberPrototype;
		} else if (c == Class.class) {
			return classPrototype;
		} else if (Number.class.isAssignableFrom(c)) {
			return numberPrototype;
		} else if (CharSequence.class.isAssignableFrom(c)) {
			return stringPrototype;
		} else if (Map.class.isAssignableFrom(c)) {
			return mapPrototype;
		} else if (Iterable.class.isAssignableFrom(c) || c.isArray()) {
			return listPrototype;
		}

		return getClassPrototype0(c);
	}

	private Prototype getClassPrototype0(Class<?> c) {
		if (classPrototypeCache == null) {
			classPrototypeCache = new HashMap<>();
		}

		var p = classPrototypeCache.get(c);

		if (p == null) {
			p = new JavaObjectPrototype(this, c);
			classPrototypeCache.put(c, p);
		}

		return p;
	}

	@Override
	public String toString() {
		return "Context";
	}

	@SuppressWarnings("unchecked")
	public <T> T adapt(Object object, Class<T> interfaceClass) {
		if (object == null) {
			return null;
		} else if (interfaceClass.isInstance(object)) {
			return (T) object;
		} else if (object instanceof Adaptable adaptable) {
			// TODO: Try to use type wrapper before interface adapter
			return adaptable.adapt(this, interfaceClass);
		}

		throw new IllegalArgumentException(object + " is not Adaptable");
	}

	public static boolean shallowEquals(Object l, Object r) {
		if (l == r) {
			return true;
		} else if (Special.isInvalid(l)) {
			return Special.isInvalid(r);
		} else if (l instanceof Number && r instanceof Number || l instanceof Boolean && r instanceof Boolean) {
			return l.equals(r);
		} else if (l instanceof CharSequence && r instanceof CharSequence) {
			return l.toString().equals(r.toString());
		}

		return l == r;
	}

	public boolean equals(Scope scope, Evaluable left, Evaluable right, boolean shallow) {
		return left == right || right.equals(scope, left, shallow) || left.equals(scope, right, shallow);
	}

	public int compareTo(Scope scope, Evaluable left, Evaluable right) {
		return Double.compare(left.evalDouble(scope), right instanceof Number n ? n.doubleValue() : Double.NaN);
	}
}
