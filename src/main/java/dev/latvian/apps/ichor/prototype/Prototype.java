package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.ConstructorError;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.java.JavaMembers;
import dev.latvian.apps.ichor.java.LocalJavaMembers;
import dev.latvian.apps.ichor.java.StaticJavaMembers;
import dev.latvian.apps.ichor.util.Empty;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Prototype<T> implements PrototypeSupplier, Callable {
	public static boolean isSingleMethodInterface(Class<?> type) {
		if (type != null && type.isInterface()) {
			var methods = type.getMethods();

			if (methods.length == 0) {
				return false;
			} else if (methods.length > 1) {
				boolean foundOne = false;

				for (var method : methods) {
					if (switch (method.getName()) {
						case "equals", "hashCode", "toString" -> false;
						default -> Modifier.isAbstract(method.getModifiers());
					}) {
						if (foundOne) {
							return false;
						} else {
							foundOne = true;
						}
					}
				}
			}

			return true;
		}

		return false;
	}

	public static class ClassLoadingError extends ScriptError {
		public final Class<?> type;

		public ClassLoadingError(Class<?> type, Throwable ex) {
			super("Failed to load class " + type.getName(), ex);
			this.type = type;
		}
	}

	private final String prototypeName;
	public final Context context;
	public final Class<T> type;
	private boolean shouldInit;
	private PrototypeConstructor constructor;
	private Map<String, PrototypeProperty> localProperties;
	private Map<String, PrototypeStaticProperty> staticProperties;
	private Boolean isSingleMethodInterface;
	protected Prototype<?>[] parents;

	@SuppressWarnings({"rawtypes", "unchecked"})
	public Prototype(Context cx, String name, Class type) {
		this.context = cx;
		this.type = type;
		this.prototypeName = name;
		this.shouldInit = true;
		this.parents = Empty.PROTOTYPES;
	}

	@SuppressWarnings("rawtypes")
	public Prototype(Context cx, Class type) {
		this(cx, type.getName(), type);
	}

	@Override
	public Prototype<T> getPrototype(Context cx, Scope scope) {
		return this;
	}

	public String getPrototypeName() {
		return prototypeName;
	}

	@SuppressWarnings("unchecked")
	public <C> C cast(Object o) {
		return (C) o;
	}

	protected void initLazy() {
		if (shouldInit) {
			shouldInit = false;
			parents = Empty.PROTOTYPES;

			if (type != getClass()) {
				initMembers();
			}

			initParents();
		}
	}

	protected void initMembers() {
		var localMap = new HashMap<String, JavaMembers>();
		var staticMap = new HashMap<String, JavaMembers>();

		try {
			for (var f : type.getDeclaredFields()) {
				if (f.getDeclaringClass() != type) {
					continue;
				}

				int mod = f.getModifiers();
				var map = Modifier.isStatic(mod) ? staticMap : localMap;
				var name = f.getName();

				var members = map.computeIfAbsent(name, JavaMembers::new);

				if (Modifier.isPublic(mod) && !Modifier.isTransient(mod)) {
					members.addField(f);
				}
			}

			for (var m : type.getDeclaredMethods()) {
				if (m.getDeclaringClass() != type) {
					continue;
				}

				int mod = m.getModifiers();
				var map = Modifier.isStatic(mod) ? staticMap : localMap;
				var name = m.getName();

				var members = map.computeIfAbsent(name, JavaMembers::new);

				if (Modifier.isPublic(mod)) {
					members.addMethod(m, map);
				}
			}
		} catch (Exception ex) {
			throw new ClassLoadingError(type, ex);
		}

		for (var e : new ArrayList<>(localMap.values())) {
			e.addFieldAccessors(localMap);
		}

		for (var e : new ArrayList<>(staticMap.values())) {
			e.addFieldAccessors(staticMap);
		}

		for (var e : localMap.values()) {
			e.prepare(this);
			property(e.name, new LocalJavaMembers(e));
		}

		for (var e : staticMap.values()) {
			e.prepare(this);
			staticProperty(e.name, new StaticJavaMembers(e));
		}
	}

	protected void initParents() {
		if (context == null) {
			return;
		}

		var p = new ArrayList<Prototype<?>>(1);
		// var p1 = new IdentityHashMap<Class<?>, Prototype<?>>();

		var s = type.getSuperclass();

		if (s != null && s != Object.class) {
			var proto = context.getClassPrototype(s);
			p.add(proto);
			// proto.unfold(p1);
		}

		for (var i : type.getInterfaces()) {
			if (i != Serializable.class && i != Cloneable.class && i != Comparable.class/* && !p1.containsKey(i)*/) {
				p.add(context.getClassPrototype(i));
			}
		}

		parent(p.toArray(Empty.PROTOTYPES));
	}

	/*
	private void unfold(Map<Class<?>, Prototype<?>> p1) {
		if (p1.put(type, this) != null) {
			return;
		}

		initLazy();

		for (var p : parents) {
			p.unfold(p1);
		}
	}
	 */

	@Override
	public String toString() {
		var sb = new StringBuilder("[JavaClass ");
		var parts = type.getName().split("\\.");

		for (int i = 0; i < parts.length - 1; i++) {
			if (parts[i].length() > 4) {
				sb.append(parts[i], 0, 3);
				sb.append('…');
			} else {
				sb.append(parts[i]);
			}

			sb.append('.');
		}

		sb.append(parts[parts.length - 1]);
		sb.append(']');
		return sb.toString();
	}

	public boolean isSingleMethodInterface() {
		if (isSingleMethodInterface == null) {
			isSingleMethodInterface = isSingleMethodInterface(type);
		}

		return isSingleMethodInterface;
	}

	// Builder //

	public void parent(Prototype<?>... parent) {
		var newParents = new Prototype[parents.length + parent.length];
		System.arraycopy(parents, 0, newParents, 0, parents.length);
		System.arraycopy(parent, 0, newParents, parents.length, parent.length);
		parents = newParents;
	}

	public void constructor(PrototypeConstructor c) {
		constructor = c;
	}

	public void property(String name, PrototypeProperty property) {
		if (localProperties == null) {
			localProperties = new HashMap<>(1);
		}

		localProperties.put(name, property);
	}

	public void function(String name, PrototypeFunction value) {
		property(name, value);
	}

	public void staticProperty(String name, PrototypeStaticProperty property) {
		if (staticProperties == null) {
			staticProperties = new HashMap<>(1);
		}

		staticProperties.put(name, property);
	}

	public void staticFunction(String name, PrototypeStaticFunction value) {
		staticProperty(name, value);
	}

	public void constant(String name, Object value) {
		staticProperty(name, new PrototypeConstant(value));
	}

	// Constructor

	@Override
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		if (constructor != null) {
			return constructor.construct(cx, scope, args, hasNew);
		}

		throw new ConstructorError(this);
	}

	// Static Named

	@Nullable
	public Object getStatic(Context cx, Scope scope, String name) {
		initLazy();

		if (staticProperties != null) {
			var m = staticProperties.get(name);

			if (m != null) {
				return m.get(cx, scope);
			}
		}

		for (var p : parents) {
			var r = p.getStatic(cx, scope, name);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		return name.equals("class") ? type : Special.NOT_FOUND;
	}

	public boolean setStatic(Context cx, Scope scope, String name, @Nullable Object value) {
		initLazy();

		if (staticProperties != null) {
			var m = staticProperties.get(name);

			if (m != null) {
				return m.set(cx, scope, value);
			}
		}

		for (var p : parents) {
			if (p.setStatic(cx, scope, name, value)) {
				return true;
			}
		}

		return false;
	}

	// Local Named

	@Nullable
	public Object getLocal(Context cx, Scope scope, T self, String name) {
		initLazy();

		if (localProperties != null) {
			var m = localProperties.get(name);

			if (m != null) {
				return m.get(cx, scope, self);
			}
		}

		for (var p : parents) {
			var r = p.getLocal(cx, scope, cast(self), name);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		return name.equals("class") ? self.getClass() : Special.NOT_FOUND;
	}

	public boolean setLocal(Context cx, Scope scope, T self, String name, @Nullable Object value) {
		initLazy();

		if (localProperties != null) {
			var m = localProperties.get(name);

			if (m != null) {
				return m.set(cx, scope, self, value);
			}
		}

		for (var p : parents) {
			if (p.setLocal(cx, scope, cast(self), name, value)) {
				return true;
			}
		}

		return false;
	}

	public boolean deleteLocal(Context cx, Scope scope, T self, String name) {
		initLazy();

		for (var p : parents) {
			if (p.deleteLocal(cx, scope, cast(self), name)) {
				return true;
			}
		}

		return false;
	}

	// Local Indexed

	@Nullable
	public Object getLocal(Context cx, Scope scope, T self, int index) {
		initLazy();

		for (var p : parents) {
			var r = p.getLocal(cx, scope, cast(self), index);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		return Special.NOT_FOUND;
	}

	public boolean setLocal(Context cx, Scope scope, T self, int index, @Nullable Object value) {
		initLazy();

		for (var p : parents) {
			if (p.setLocal(cx, scope, cast(self), index, value)) {
				return true;
			}
		}

		return false;
	}

	public boolean deleteLocal(Context cx, Scope scope, T self, int index) {
		initLazy();

		for (var p : parents) {
			if (p.deleteLocal(cx, scope, cast(self), index)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	public Collection<?> keys(Context cx, Scope scope, T self) {
		initLazy();

		for (var p : parents) {
			var r = p.keys(cx, scope, cast(self));

			if (r != null) {
				return r;
			}
		}

		return null;
	}

	@Nullable
	public Collection<?> values(Context cx, Scope scope, T self) {
		initLazy();

		for (var p : parents) {
			var r = p.values(cx, scope, cast(self));

			if (r != null) {
				return r;
			}
		}

		return null;
	}

	@Nullable
	public Collection<?> entries(Context cx, Scope scope, T self) {
		initLazy();

		for (var p : parents) {
			var r = p.entries(cx, scope, cast(self));

			if (r != null) {
				return r;
			}
		}

		return null;
	}

	public boolean asString(Context cx, Scope scope, T self, StringBuilder builder, boolean escape) {
		initLazy();

		for (var p : parents) {
			if (p.asString(cx, scope, cast(self), builder, escape)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	public Number asNumber(Context cx, Scope scope, T self) {
		initLazy();

		for (var p : parents) {
			var r = p.asNumber(cx, scope, cast(self));

			if (r != null) {
				return r;
			}
		}

		return null;
	}

	@Nullable
	public Boolean asBoolean(Context cx, Scope scope, T self) {
		initLazy();

		for (var p : parents) {
			var r = p.asBoolean(cx, scope, cast(self));

			if (r != null) {
				return r;
			}
		}

		return null;
	}

	public boolean equals(Context cx, Scope scope, T left, Object right, boolean shallow) {
		return shallow ? left == right : Objects.equals(left, right);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public int compareTo(Context cx, Scope scope, T left, Object right) {
		if (left instanceof Comparable l && right instanceof Comparable r) {
			return l.compareTo(r);
		}

		return 0;
	}

	@Nullable
	public Object adapt(Context cx, Scope scope, Object self, @Nullable Class<?> toType) {
		initLazy();

		for (var p : parents) {
			var r = p.adapt(cx, scope, self, toType);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		return Special.NOT_FOUND;
	}
}
