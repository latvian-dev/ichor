package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import dev.latvian.apps.ichor.util.Signature;

import java.lang.reflect.Constructor;
import java.util.Map;

public class JavaTypePrototype extends PrototypeBuilder {
	private record SigConstructor(Signature signature, Constructor<?> constructor) {
	}

	public final Context context;
	public final Class<?> type;
	private SigConstructor[] constructors;
	private Map<String, Object> members;

	private boolean shouldInit;

	private void init0() {
		if (shouldInit) {
			shouldInit = false;
			synchronized (this) {
				try {
					for (var f : type.getDeclaredFields()) {
					}

					for (var m : type.getDeclaredMethods()) {
					}
				} catch (Exception ex) {
					throw new ScriptError("Failed to load class " + type.getName(), ex);
				}
			}
		}
	}

	public JavaTypePrototype(Context cx, Class<?> t) {
		super(t.getName());
		context = cx;
		type = t;
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
