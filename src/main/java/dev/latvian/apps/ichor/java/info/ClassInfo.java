package dev.latvian.apps.ichor.java.info;

import dev.latvian.apps.ichor.api.Hidden;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class ClassInfo {
	public static final ClassInfo[] EMPTY = new ClassInfo[0];

	public static final Object LOCK = new Object();
	private static final Map<Class<?>, ClassInfo> CACHE = new IdentityHashMap<>();

	public static ClassInfo of(Class<?> c) {
		synchronized (LOCK) {
			return CACHE.computeIfAbsent(c, ClassInfo::new);
		}
	}

	public final Class<?> wrapped;

	private ClassInfo[] parents;
	private FieldInfo[] fields;
	private MethodInfo[] methods;
	private String[] remapPrefixes;

	public ClassInfo(Class<?> wrapped) {
		this.wrapped = wrapped;
	}

	public ClassInfo[] getParents() {
		if (parents == null) {
			var set = new LinkedHashSet<ClassInfo>();

			var s = wrapped.getSuperclass();

			if (s != null) {
				set.add(of(s));
			}

			for (var iface : wrapped.getInterfaces()) {
				set.add(of(iface));
			}

			parents = set.toArray(EMPTY);
		}

		return parents;
	}

	public FieldInfo[] getFields() {
		if (fields == null) {
			var list = new ArrayList<FieldInfo>();

			for (var f : wrapped.getDeclaredFields()) {
				int m = f.getModifiers();

				if (Modifier.isPublic(m) && !Modifier.isTransient(m) && !f.isAnnotationPresent(Hidden.class)) {
					list.add(new FieldInfo(this, f));
				}
			}

			fields = list.toArray(FieldInfo.EMPTY);
		}

		return fields;
	}

	public MethodInfo[] getMethods() {
		if (methods == null) {
			var list = new ArrayList<MethodInfo>();

			for (var m : wrapped.getDeclaredMethods()) {
				int mod = m.getModifiers();

				if (Modifier.isPublic(mod)) {
					list.add(new MethodInfo(this, m));
				}
			}

			methods = list.toArray(MethodInfo.EMPTY);
		}

		return methods;
	}
}
