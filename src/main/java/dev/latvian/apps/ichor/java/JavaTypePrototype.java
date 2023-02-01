package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeProperty;
import dev.latvian.apps.ichor.prototype.PrototypeStaticProperty;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JavaTypePrototype extends Prototype {
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

	public final Context context;
	public final Class<?> type;
	private boolean shouldInit;
	private Boolean isSingleMethodInterface;
	protected Prototype[] parents;
	protected Map<String, PrototypeProperty> localMembers;
	protected Map<String, PrototypeStaticProperty> staticMembers;

	public JavaTypePrototype(Context cx, Class<?> type) {
		super(type.getName());
		this.context = cx;
		this.type = type;
		this.shouldInit = true;
	}

	protected void initLazy() {
		if (shouldInit) {
			shouldInit = false;
			initMembers();
			initParents();
		}
	}

	protected void initMembers() {
		var localMap = new HashMap<String, JavaMembers>();
		var staticMap = new HashMap<String, JavaMembers>();

		try {
			for (var f : type.getDeclaredFields()) {
				int mod = f.getModifiers();
				var map = Modifier.isStatic(mod) ? staticMap : localMap;
				var name = f.getName();

				var members = map.computeIfAbsent(name, JavaMembers::new);

				if (Modifier.isPublic(mod) && !Modifier.isTransient(mod)) {
					members.addField(f);
				}
			}

			for (var m : type.getDeclaredMethods()) {
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

		localMembers = localMap.isEmpty() ? Map.of() : new HashMap<>(localMap.size());
		staticMembers = staticMap.isEmpty() ? Map.of() : new HashMap<>(staticMap.size());

		for (var e : localMap.values()) {
			e.prepare(this);
			localMembers.put(e.name, new LocalJavaMembers(e));
		}

		for (var e : staticMap.values()) {
			e.prepare(this);
			staticMembers.put(e.name, new StaticJavaMembers(e));
		}
	}

	protected void initParents() {
		var p = new ArrayList<Prototype>();

		var s = type.getSuperclass();

		if (s != null && s != Object.class) {
			p.add(context.getClassPrototype(s));
		}

		for (var i : type.getInterfaces()) {
			if (i != Serializable.class && i != Cloneable.class && i != Comparable.class) {
				p.add(context.getClassPrototype(i));
			}
		}

		parents = p.toArray(new Prototype[0]);
	}

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

	@Override
	public boolean isSingleMethodInterface() {
		if (isSingleMethodInterface == null) {
			isSingleMethodInterface = isSingleMethodInterface(type);
		}

		return isSingleMethodInterface;
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		initLazy();

		var m = staticMembers.get(name);

		if (m != null) {
			var r = m.get(cx, scope);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		for (var p : parents) {
			var r = p.get(cx, scope, name);

				if (r != Special.NOT_FOUND) {
					return r;
			}
		}

		return name.equals("class") ? type : super.get(cx, scope, name);
	}

	@Override
	public boolean set(Context cx, Scope scope, String name, @Nullable Object value) {
		initLazy();

		var m = staticMembers.get(name);

		if (m != null && m.set(cx, scope, value)) {
			return true;
		}

		for (var p : parents) {
			if (p.set(cx, scope, name, value)) {
				return true;
			}
		}

		return !name.equals("class") && super.set(cx, scope, name, value);
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, Object self, String name) {
		initLazy();

		var m = localMembers.get(name);

		if (m != null) {
			var r = m.get(cx, scope, self);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		for (var p : parents) {
			var r = p.get(cx, scope, self, name);

			if (r != Special.NOT_FOUND) {
				return r;
			}
		}

		return name.equals("class") ? self.getClass() : super.get(cx, scope, self, name);
	}

	@Override
	public boolean set(Context cx, Scope scope, Object self, String name, @Nullable Object value) {
		initLazy();

		var m = localMembers.get(name);

		if (m != null && m.set(cx, scope, self, value)) {
			return true;
		}

		for (var p : parents) {
			if (p.set(cx, scope, self, name, value)) {
				return true;
			}
		}

		return !name.equals("class") && super.set(cx, scope, self, name, value);
	}
}
