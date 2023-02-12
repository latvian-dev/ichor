package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.CastError;
import dev.latvian.apps.ichor.error.InternalScriptError;
import dev.latvian.apps.ichor.java.AnnotatedElementPrototype;
import dev.latvian.apps.ichor.java.JavaClassPrototype;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.IchorUtils;
import dev.latvian.apps.ichor.util.JavaArray;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class Context {
	private final Map<Class<?>, Prototype<?>> classPrototypes;
	private int maxScopeDepth;
	private long interpretingTimeout;
	private long tokenStreamTimeout;
	private Remapper remapper;

	public Context() {
		classPrototypes = new IdentityHashMap<>();
		maxScopeDepth = 1000;
		interpretingTimeout = 30000L;
		tokenStreamTimeout = 5000L;

		registerPrototype(new JavaClassPrototype(this));
		registerPrototype(new AnnotatedElementPrototype(this));
	}

	public List<Prototype<?>> getSafePrototypes() {
		return Collections.emptyList();
	}

	public void registerPrototype(Prototype<?> prototype) {
		classPrototypes.put(prototype.type, prototype);
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

	@Nullable
	public Remapper getRemapper() {
		return remapper;
	}

	public void setRemapper(Remapper r) {
		remapper = r;
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
		return n == null ? IchorUtils.ONE : n;
	}

	public Number asNumber(Scope scope, Object o) {
		if (Special.isInvalid(o)) {
			return IchorUtils.NaN;
		} else if (o instanceof Number) {
			return (Number) o;
		} else if (o instanceof Boolean) {
			return (Boolean) o ? IchorUtils.ONE : IchorUtils.ZERO;
		} else if (o instanceof CharSequence) {
			try {
				return IchorUtils.parseNumber(o.toString());
			} catch (Exception ex) {
				return IchorUtils.NaN;
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
				return IchorUtils.parseNumber(o.toString()).doubleValue();
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
				return IchorUtils.parseNumber(o.toString()).intValue();
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
				return IchorUtils.parseNumber(o.toString()).longValue();
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

		throw new CastError(o, "Character");
	}

	@SuppressWarnings("rawtypes")
	public Class asClass(Scope scope, Object o) {
		return as(scope, o, Class.class);
	}

	@SuppressWarnings("unchecked")
	public <T> T as(Scope scope, Object o, @Nullable Class<T> toType) {
		if (Special.isInvalid(o)) {
			return null;
		} else if (toType == null || toType == Void.TYPE || toType == Object.class || toType == o.getClass() || toType.isInstance(o)) {
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
			return (T) Integer.valueOf(asInt(scope, o));
		} else if (toType == Long.class || toType == Long.TYPE) {
			return (T) Long.valueOf(asLong(scope, o));
		} else if (toType == Float.class || toType == Float.TYPE) {
			return (T) Float.valueOf(asNumber(scope, o).floatValue());
		} else if (toType == Double.class || toType == Double.TYPE) {
			return (T) Double.valueOf(asDouble(scope, o));
		} else if (o instanceof TypeAdapter typeAdapter && typeAdapter.canAdapt(this, toType)) {
			return typeAdapter.adapt(this, scope, toType);
		}

		var c = customAs(scope, o, toType);

		if (c != Special.NOT_FOUND) {
			return (T) c;
		}

		var a = getPrototype(scope, o).adapt(this, scope, o, toType);

		if (a != Special.NOT_FOUND) {
			return (T) a;
		} else if (o instanceof Iterable<?> itr && toType.isArray()) {
			return (T) JavaArray.adaptToArray(this, scope, itr, toType);
		} else {
			throw new CastError(o, toType.getName());
		}
	}

	@Nullable
	private <T> Object customAs(Scope scope, Object o, Class<?> toType) {
		return Special.NOT_FOUND;
	}

	public Prototype<?> getPrototype(Scope scope, Object o) {
		return getClassPrototype(o.getClass());
	}

	public Prototype<?> getClassPrototype(Class<?> c) {
		var p = classPrototypes.get(c);

		if (p == null) {
			p = new Prototype<>(this, c);
			classPrototypes.put(c, p);
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
