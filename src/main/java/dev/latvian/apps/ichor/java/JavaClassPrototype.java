package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class JavaClassPrototype extends PrototypeBuilder {
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

	public JavaClassPrototype(Context cx, Class<?> type) {
		super(type.getName());
		this.context = cx;
		this.type = type;
		this.shouldInit = true;
	}

	@Override
	protected void initLazy() {
		if (shouldInit) {
			shouldInit = false;

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

			for (var e : localMap.values()) {
				e.prepare(this);
				property(e.name, new LocalJavaMembers(e));
			}

			for (var e : staticMap.values()) {
				e.prepare(this);
				staticProperty(e.name, new StaticJavaMembers(e));
			}

			property("class", (cx, scope, self) -> self.getClass());
			constant("class", type);
		}
	}

	@Override
	public String toString() {
		return type.getName();
	}

	/*
	@Override
	public Ref getParentRef() {
		if (parent == null) {
			var set = new ArrayList<Ref>();

			if (!type.isPrimitive()) {
				var s = type.getSuperclass();

				if (s != null && s != Object.class) {
					set.add(context.getClassPrototype(s));
				}

				for (var iface : type.getInterfaces()) {
					set.add(context.getClassPrototype(iface));
				}
			}

			parent = RefArray.of(set);
		}

		return parent;
	}

	@Override
	public Object construct(Context cx, Object[] args, boolean hasNew) {
		if (!hasNew) {
			if (args.length == 1) {
				return cx.as(args[0], type);
			}

			return Special.NOT_FOUND;
		}

		if (constructors == null) {
			synchronized (this) {
				var carr = type.getDeclaredConstructors();
				var list = new ArrayList<SigConstructor>(carr.length);

				for (var c : carr) {
					if (Modifier.isPublic(c.getModifiers()) && !c.isAnnotationPresent(HideFromJS.class)) {
						list.add(new SigConstructor(Signature.of(c), c));
					}
				}

				constructors = list.toArray(new SigConstructor[0]);
			}
		}

		for (var c : constructors) {
			if (c.signature.types.length == args.length) {

			}
		}

		return Special.NOT_FOUND;
	}
	 */
}
