package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Remapper;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.annotation.Hidden;
import dev.latvian.apps.ichor.annotation.Remap;
import dev.latvian.apps.ichor.annotation.RemapPrefix;
import dev.latvian.apps.ichor.error.ConstructorError;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.java.JavaMembers;
import dev.latvian.apps.ichor.java.LocalJavaMembers;
import dev.latvian.apps.ichor.java.StaticJavaMembers;
import dev.latvian.apps.ichor.util.Empty;
import dev.latvian.apps.ichor.util.Signature;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
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
	private PrototypeConstructor constructor;
	private Map<String, PrototypeProperty> localProperties;
	private Map<String, PrototypeStaticProperty> staticProperties;
	private Boolean isSingleMethodInterface;
	private Prototype<?>[] parents;
	private Map<Class<?>, PrototypeTypeAdapter<T>> typeAdapters;

	@SuppressWarnings({"rawtypes", "unchecked"})

	public Prototype(Context cx, String name, Class type) {
		this.context = cx;
		this.type = type;
		this.prototypeName = name;
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

	public Prototype<?>[] getParents() {
		if (parents == null) {
			parents = Empty.PROTOTYPES;
			initParents();
		}

		return parents;
	}

	protected void initProperties() {
		if (staticProperties == null || localProperties == null) {
			staticProperties = Map.of();
			localProperties = Map.of();

			if (type != getClass()) {
				initMembers();
			}
		}
	}

	protected void initMembers() {
		var localMap = new HashMap<String, JavaMembers>();
		var staticMap = new HashMap<String, JavaMembers>();

		var remapper = context.getRemapper();

		try {
			var prefixes0 = new HashSet<String>(0);

			for (var a : type.getAnnotationsByType(RemapPrefix.class)) {
				prefixes0.add(a.value());
			}

			var prefixes = prefixes0.toArray(Empty.STRINGS);

			for (var f : type.getDeclaredFields()) {
				if (f.getDeclaringClass() != type) {
					continue;
				}

				int mod = f.getModifiers();

				if (!Modifier.isPublic(mod) || Modifier.isTransient(mod) || f.isAnnotationPresent(Hidden.class)) {
					continue;
				}

				var map = Modifier.isStatic(mod) ? staticMap : localMap;
				var members = map.computeIfAbsent(getFieldName(prefixes, remapper, f), JavaMembers::new);
				members.addField(f);
			}

			for (var m : type.getDeclaredMethods()) {
				if (m.getDeclaringClass() != type) {
					continue;
				}

				int mod = m.getModifiers();

				if (!Modifier.isPublic(mod) || m.isAnnotationPresent(Hidden.class)) {
					continue;
				}

				var map = Modifier.isStatic(mod) ? staticMap : localMap;
				var signature = Signature.of(m);
				var members = map.computeIfAbsent(getMethodName(prefixes, remapper, m, signature), JavaMembers::new);
				members.addMethod(m, signature, map);
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

	private String getFieldName(String[] prefixes, @Nullable Remapper remapper, Field field) {
		var r = field.getAnnotation(Remap.class);

		if (r != null) {
			return r.value();
		}

		return remapper != null ? remapper.getFieldName(context, field) : field.getName();
	}

	private String getMethodName(String[] prefixes, @Nullable Remapper remapper, Method method, Signature signature) {
		var r = method.getAnnotation(Remap.class);

		if (r != null) {
			return r.value();
		}

		return remapper != null ? remapper.getMethodName(context, method, signature) : method.getName();
	}

	protected void initParents() {
		if (context == null) {
			return;
		}

		var p = new ArrayList<Prototype<?>>(1);
		// var p1 = new IdentityHashMap<Class<?>, Prototype<?>>();

		for (var i : type.getInterfaces()) {
			if (i != Serializable.class && i != Cloneable.class && i != Comparable.class/* && !p1.containsKey(i)*/) {
				p.add(context.getClassPrototype(i));
			}
		}

		var s = type.getSuperclass();

		if (s != null && s != Object.class) {
			var proto = context.getClassPrototype(s);
			p.add(proto);
			// proto.unfold(p1);
		}

		parents = p.toArray(Empty.PROTOTYPES);
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
		getParents();
		var newParents = new Prototype[parents.length + parent.length];
		System.arraycopy(parents, 0, newParents, 0, parents.length);
		System.arraycopy(parent, 0, newParents, parents.length, parent.length);
		parents = newParents;
	}

	public void constructor(PrototypeConstructor c) {
		constructor = c;
	}

	public void property(String name, PrototypeProperty property) {
		initProperties();

		if (localProperties.isEmpty()) {
			localProperties = new HashMap<>(1);
		}

		localProperties.put(name, property);
	}

	public void function(String name, PrototypeFunction value) {
		property(name, value);
	}

	public void staticProperty(String name, PrototypeStaticProperty property) {
		initProperties();

		if (staticProperties.isEmpty()) {
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

	public void typeAdapter(PrototypeTypeAdapter<T> typeAdapter, Class<?> toType) {
		if (typeAdapters == null) {
			typeAdapters = new IdentityHashMap<>();
		}

		var f = typeAdapters.get(toType);

		if (f == null) {
			typeAdapters.put(toType, typeAdapter);
		} else {
			typeAdapters.put(toType, new PrototypeTypeAdapter.Fallback<>(typeAdapter, f));
		}
	}

	public void typeAdapter(PrototypeTypeAdapter<T> typeAdapter, Class<?>... toTypes) {
		for (var toType : toTypes) {
			typeAdapter(typeAdapter, toType);
		}
	}

	// Constructor

	@Override
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		initProperties();

		if (constructor != null) {
			return constructor.construct(cx, scope, args, hasNew);
		}

		throw new ConstructorError(this);
	}

	// Internal getter methods

	@Nullable
	public Object getInternal(Context cx, Scope scope, Object self, String name) {
		return self == this ? getStatic(cx, scope, name) : getLocal(cx, scope, cast(self), name);
	}

	// Static Named

	@Nullable
	public Object getStatic(Context cx, Scope scope, String name) {
		initProperties();

		var m = staticProperties.get(name);

		if (m != null) {
			return m.get(cx, scope);
		}

		for (var p : getParents()) {
			var r = p.getStatic(cx, scope, name);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		return name.equals("class") ? type : Special.NOT_FOUND;
	}

	public boolean setStatic(Context cx, Scope scope, String name, @Nullable Object value) {
		initProperties();

		var m = staticProperties.get(name);

		if (m != null) {
			return m.set(cx, scope, value);
		}

		for (var p : getParents()) {
			if (p.setStatic(cx, scope, name, value)) {
				return true;
			}
		}

		return false;
	}

	// Local Named

	@Nullable
	public Object getLocal(Context cx, Scope scope, T self, String name) {
		initProperties();

		var m = localProperties.get(name);

		if (m != null) {
			return m.get(cx, scope, self);
		}

		for (var p : getParents()) {
			var r = p.getLocal(cx, scope, cast(self), name);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		return name.equals("class") ? self.getClass() : Special.NOT_FOUND;
	}

	public boolean setLocal(Context cx, Scope scope, T self, String name, @Nullable Object value) {
		initProperties();

		var m = localProperties.get(name);

		if (m != null) {
			return m.set(cx, scope, self, value);
		}

		for (var p : getParents()) {
			if (p.setLocal(cx, scope, cast(self), name, value)) {
				return true;
			}
		}

		return false;
	}

	public boolean deleteLocal(Context cx, Scope scope, T self, String name) {
		for (var p : getParents()) {
			if (p.deleteLocal(cx, scope, cast(self), name)) {
				return true;
			}
		}

		return false;
	}

	// Local Indexed

	@Nullable
	public Object getLocal(Context cx, Scope scope, T self, int index) {
		for (var p : getParents()) {
			var r = p.getLocal(cx, scope, cast(self), index);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		return Special.NOT_FOUND;
	}

	public boolean setLocal(Context cx, Scope scope, T self, int index, @Nullable Object value) {
		for (var p : getParents()) {
			if (p.setLocal(cx, scope, cast(self), index, value)) {
				return true;
			}
		}

		return false;
	}

	public boolean deleteLocal(Context cx, Scope scope, T self, int index) {
		for (var p : getParents()) {
			if (p.deleteLocal(cx, scope, cast(self), index)) {
				return true;
			}
		}

		return false;
	}

	// Iterators

	@Nullable
	public Collection<?> keys(Context cx, Scope scope, T self) {
		for (var p : getParents()) {
			var r = p.keys(cx, scope, cast(self));

			if (r != null) {
				return r;
			}
		}

		return null;
	}

	@Nullable
	public Collection<?> values(Context cx, Scope scope, T self) {
		for (var p : getParents()) {
			var r = p.values(cx, scope, cast(self));

			if (r != null) {
				return r;
			}
		}

		return null;
	}

	@Nullable
	public Collection<?> entries(Context cx, Scope scope, T self) {
		for (var p : getParents()) {
			var r = p.entries(cx, scope, cast(self));

			if (r != null) {
				return r;
			}
		}

		return null;
	}

	// Conversions

	public boolean asString(Context cx, Scope scope, T self, StringBuilder builder, boolean escape) {
		for (var p : getParents()) {
			if (p.asString(cx, scope, cast(self), builder, escape)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	public Number asNumber(Context cx, Scope scope, T self) {
		for (var p : getParents()) {
			var r = p.asNumber(cx, scope, cast(self));

			if (r != null) {
				return r;
			}
		}

		return null;
	}

	@Nullable
	public Boolean asBoolean(Context cx, Scope scope, T self) {
		for (var p : getParents()) {
			var r = p.asBoolean(cx, scope, cast(self));

			if (r != null) {
				return r;
			}
		}

		return null;
	}

	@Nullable
	public Object adapt(Context cx, Scope scope, T self, @Nullable Class<?> toType) {
		if (typeAdapters != null) {
			var a = typeAdapters.get(toType);

			if (a != null) {
				var r = a.adapt(cx, scope, self, toType);

				if (r != Special.NOT_FOUND) {
					return r;
				}
			}
		}

		for (var p : getParents()) {
			var r = p.adapt(cx, scope, cast(self), toType);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		return Special.NOT_FOUND;
	}

	// Equality

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
}
