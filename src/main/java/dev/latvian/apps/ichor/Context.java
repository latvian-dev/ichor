package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.CastError;
import dev.latvian.apps.ichor.error.InternalScriptError;
import dev.latvian.apps.ichor.java.AnnotatedElementPrototype;
import dev.latvian.apps.ichor.java.JavaClassPrototype;
import dev.latvian.apps.ichor.js.TokenStreamJS;
import dev.latvian.apps.ichor.js.type.NumberJS;
import dev.latvian.apps.ichor.prototype.Prototype;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class Context {
	private Map<Class<?>, Prototype<?>> classPrototypeCache;
	private final Map<ContextProperty<?>, Object> properties;
	private int maxScopeDepth;
	private long interpretingTimeout;
	private long tokenStreamTimeout;
	private Object environment;

	public Context() {
		properties = new IdentityHashMap<>();
		maxScopeDepth = 1000;
		interpretingTimeout = 30000L;
		tokenStreamTimeout = 5000L;
		environment = Special.NOT_FOUND;
	}

	public List<Prototype<?>> getSafePrototypes() {
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperty(ContextProperty<T> property) {
		var val = properties == null ? null : properties.get(property);
		return val == null ? property.defaultValue() : (T) val;
	}

	public <T> void setProperty(ContextProperty<T> property, T value) {
		properties.put(property, value);
	}

	public int getMaxScopeDepth() {
		return maxScopeDepth;
	}

	public void setMaxScopeDepth(int maxScopeDepth) {
		this.maxScopeDepth = maxScopeDepth;
	}

	public long getInterpretingTimeout() {
		return interpretingTimeout;
	}

	public void setInterpretingTimeout(long interpretingTimeout) {
		this.interpretingTimeout = interpretingTimeout;
	}

	public long getTokenStreamTimeout() {
		return tokenStreamTimeout;
	}

	public void setTokenStreamTimeout(long tokenStreamTimeout) {
		this.tokenStreamTimeout = tokenStreamTimeout;
	}

	public Object getEnvironment() {
		return environment;
	}

	public void setEnvironment(Object environment) {
		this.environment = environment;
	}

	public Object eval(Scope scope, Object o) {
		if (o instanceof Callable) {
			return o;
		} else if (o instanceof Evaluable eval) {
			return eval.eval(this, scope);
		} else {
			return o;
		}
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
			var p = getPrototype(scope, o);

			if (o == p || !p.asString(this, scope, p.cast(o), builder, escape)) {
				builder.append(o);
			}
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
			var p = getPrototype(scope, o);

			if (o == p) {
				return o.toString();
			}

			var builder = new StringBuilder();

			if (!p.asString(this, scope, p.cast(o), builder, escape)) {
				return o.toString();
			}

			return builder.toString();
		}
	}

	private Number asNumber0(Scope scope, Object o) {
		var p = getPrototype(scope, o);
		var n = o == p ? null : p.asNumber(this, scope, p.cast(o));
		return n == null ? NumberJS.ONE : n;
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
				return TokenStreamJS.parseNumber(o.toString());
			} catch (Exception ex) {
				return NumberJS.NaN;
			}
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalDouble(this, scope);
		}

		return asNumber0(scope, o);
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
				return TokenStreamJS.parseNumber(o.toString()).doubleValue();
			} catch (Exception ex) {
				return Double.NaN;
			}
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalDouble(this, scope);
		}

		return asNumber0(scope, o).doubleValue();
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
				return TokenStreamJS.parseNumber(o.toString()).intValue();
			} catch (Exception ex) {
				throw new InternalScriptError(ex);
			}
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalInt(this, scope);
		}

		return asNumber0(scope, o).intValue();
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
				return TokenStreamJS.parseNumber(o.toString()).longValue();
			} catch (Exception ex) {
				throw new InternalScriptError(ex);
			}
		} else if (o instanceof Evaluable) {
			// add evalLong
			return ((Evaluable) o).evalInt(this, scope);
		}

		return asNumber0(scope, o).longValue();
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

		var p = getPrototype(scope, o);
		var n = o == p ? null : p.asBoolean(this, scope, p.cast(o));
		return n == null ? Boolean.TRUE : n;
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

	public Prototype<?> getPrototype(Scope scope, Object o) {
		return getClassPrototype(o.getClass());
	}

	public Prototype<?> getClassPrototype(Class<?> c) {
		if (classPrototypeCache == null) {
			classPrototypeCache = new IdentityHashMap<>();
		}

		var p = classPrototypeCache.get(c);

		if (p == null) {
			p = createJavaPrototype(c);
			classPrototypeCache.put(c, p);
		}

		return p;
	}

	protected Prototype<?> createJavaPrototype(Class<?> type) {
		if (type == Class.class) {
			return new JavaClassPrototype(this);
		} else if (type == AnnotatedElement.class) {
			return new AnnotatedElementPrototype(this);
		}

		return new Prototype<>(this, type);
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
		} else {
			var p = getPrototype(scope, left);
			return p.equals(this, scope, p.cast(left), right, shallow);
		}
	}

	public int compareTo(Scope scope, Object left, Object right) {
		if (left == right || Objects.equals(left, right)) {
			return 0;
		} else if (left instanceof Number l && right instanceof Number r) {
			return Math.abs(l.doubleValue() - r.doubleValue()) < 0.00001D ? 0 : Double.compare(l.doubleValue(), r.doubleValue());
		} else {
			var p = getPrototype(scope, right);
			return p.compareTo(this, scope, p.cast(left), right);
		}
	}
}
