package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import dev.latvian.apps.ichor.prototype.PrototypeFunction;
import dev.latvian.apps.ichor.prototype.PrototypeProperty;
import dev.latvian.apps.ichor.util.Empty;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JavaObjectPrototype extends PrototypeBuilder {
	private record FieldProperty(Field field) implements PrototypeProperty {
		@Override
		public Object get(Context cx, Scope scope, Object self) {
			try {
				return field.get(self);
			} catch (Exception ex) {
				return Special.NOT_FOUND;
			}
		}

		@Override
		public boolean set(Context cx, Scope scope, Object self, @Nullable Object value) {
			try {
				field.set(self, cx.as(scope, value, field.getType()));
				return true;
			} catch (Exception ex) {
				return false;
			}
		}

		@Override
		public String toString() {
			return "JavaField[" + field + "]";
		}
	}

	private record MethodFunction(Method method, Class<?>[] methodParams) implements PrototypeFunction {
		@Override
		public Object call(Context cx, Scope scope, Object self, Object[] args) {
			try {
				if (methodParams.length > 0) {
					var args1 = new Object[methodParams.length];

					for (int i = 0; i < args1.length; i++) {
						args1[i] = i >= args.length ? null : cx.as(scope, args[i], methodParams[i]);
					}

					return method.invoke(self, args1);
				} else {
					return method.invoke(self, Empty.OBJECTS);
				}
			} catch (Exception ex) {
				return Special.NOT_FOUND;
			}
		}

		@Override
		public String toString() {
			return "JavaMethod[" + method + "]";
		}
	}

	public final Context context;
	public final Class<?> type;
	private boolean shouldInit;

	public JavaObjectPrototype(Context cx, Class<?> t) {
		super(t.getName());
		context = cx;
		type = t;
		shouldInit = true;
	}

	@Override
	protected void initLazy() {
		if (shouldInit) {
			shouldInit = false;

			try {
				for (var f : type.getDeclaredFields()) {
					int mod = f.getModifiers();

					if (Modifier.isPublic(mod) && !Modifier.isTransient(mod)) {
						property(f.getName(), new FieldProperty(f));
					}
				}

				for (var m : type.getDeclaredMethods()) {
					int mod = m.getModifiers();

					if (Modifier.isPublic(mod)) {
						function(m.getName(), new MethodFunction(m, m.getParameterCount() == 0 ? Empty.CLASSES : m.getParameterTypes()));
					}
				}
			} catch (Exception ex) {
				throw new ScriptError("Failed to load class " + type.getName(), ex);
			}

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
