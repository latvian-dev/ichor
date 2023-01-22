package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.CastError;
import dev.latvian.apps.ichor.java.ClassJS;
import dev.latvian.apps.ichor.java.JavaClassPrototype;
import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
		classPrototype = ClassJS.PROTOTYPE;
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

	public Object eval(Scope scope, Object o) {
		return o instanceof Callable ? o : o instanceof Evaluable eval ? eval.eval(this, scope) : o;
	}

	public void asString(Scope scope, Object o, StringBuilder builder, boolean escape) {
		if (o == null) {
			builder.append("null");
		} else if (o instanceof Number) {
			AstStringBuilder.wrapNumber(o, builder);
		} else if (o instanceof Character || o instanceof CharSequence) {
			if (escape) {
				AstStringBuilder.wrapString(o, builder);
			} else {
				builder.append(o);
			}
		} else if (o instanceof Boolean || o instanceof Special) {
			builder.append(o);
		} else if (o instanceof Evaluable eval) {
			eval.evalString(this, scope, builder);
		} else {
			getPrototype(scope, o).asString(this, scope, o, builder, escape);
		}
	}

	public String asString(Scope scope, Object o, boolean escape) {
		if (o == null) {
			return "null";
		} else if (o instanceof Number) {
			return AstStringBuilder.wrapNumber(o);
		} else if (o instanceof Character || o instanceof CharSequence) {
			if (escape) {
				var builder = new StringBuilder();
				AstStringBuilder.wrapString(o, builder);
				return builder.toString();
			} else {
				return o.toString();
			}
		} else if (o instanceof Boolean || o instanceof Special) {
			return o.toString();
		} else if (o instanceof Evaluable eval) {
			var builder = new StringBuilder();
			eval.evalString(this, scope, builder);
			return builder.toString();
		} else {
			var builder = new StringBuilder();
			getPrototype(scope, o).asString(this, scope, o, builder, escape);
			return builder.toString();
		}
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
			return ((Evaluable) o).evalDouble(this, scope);
		}

		return getPrototype(scope, o).asNumber(this, scope, o);
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
			return ((Evaluable) o).evalDouble(this, scope);
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
			return ((Evaluable) o).evalInt(this, scope);
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
			return ((Evaluable) o).evalInt(this, scope);
		}

		return asNumber(scope, o).longValue();
	}

	public boolean asBoolean(Scope scope, Object o) {
		if (o instanceof Boolean) {
			return (Boolean) o;
		} else if (Special.isInvalid(o)) {
			return false;
		} else if (o instanceof Number) {
			return ((Number) o).doubleValue() != 0D;
		} else if (o instanceof CharSequence) {
			return !o.toString().isEmpty();
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalBoolean(this, scope);
		}

		return getPrototype(scope, o).asBoolean(this, scope, o);
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
			((Evaluable) o).evalString(this, scope, builder);
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
			return (T) asString(scope, o, false);
		} else if (toType == Number.class) {
			return (T) asNumber(scope, o);
		} else if (toType == Boolean.class || toType == Boolean.TYPE) {
			return (T) Boolean.valueOf(asBoolean(scope, o));
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
		} else if (o instanceof TypeAdapter typeAdapter && typeAdapter.canAdapt(this, toType)) {
			return typeAdapter.adapt(this, scope, toType);
		}

		throw new CastError(o.getClass().getName(), toType.getName());
	}

	public Prototype getPrototype(Scope scope, Object o) {
		if (o == null) {
			return Special.NULL.prototype;
		} else if (o instanceof CharSequence) {
			return stringPrototype;
		} else if (o instanceof Number) {
			return numberPrototype;
		} else if (o instanceof Boolean) {
			return booleanPrototype;
		} else if (o instanceof PrototypeSupplier s) {
			return s.getPrototype(this, scope);
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
			return Special.NULL.prototype;
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
			p = new JavaClassPrototype(this, c);
			classPrototypeCache.put(c, p);
		}

		return p;
	}

	@Override
	public String toString() {
		return "Context";
	}

	public boolean equals(Scope scope, Object left, Object right, boolean shallow) {
		if (left == right) {
			return true;
		} else if (left instanceof Number l && right instanceof Number r) {
			return Math.abs(l.doubleValue() - r.doubleValue()) < 0.00001D;
		} else if (left instanceof CharSequence || left instanceof Character || right instanceof CharSequence || right instanceof Character) {
			return asString(scope, left, false).equals(asString(scope, right, false));
		}

		return Objects.equals(left, right); // prototype equals
	}

	public int compareTo(Scope scope, Object left, Object right) {
		if (left == right || Objects.equals(left, right)) {
			return 0;
		} else if (left instanceof Number l && right instanceof Number r) {
			return Double.compare(l.doubleValue(), r.doubleValue());
		} else {
			return 0; // prototype compare
		}
	}
}
