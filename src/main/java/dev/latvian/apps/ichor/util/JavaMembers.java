package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.js.java.JavaTypePrototype;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JavaMembers {
	public record SigMethod(Signature signature, Method method) {
	}

	public record BeanSetter(Class<?> argType, Method method) {
	}

	private final JavaTypePrototype prototype;
	private final String name;
	public Field field;
	public SigMethod[] methods;
	public Method beanGetter;
	public BeanSetter[] beanSetters;

	public JavaMembers(JavaTypePrototype p, String n) {
		prototype = p;
		name = n;
	}

	public Object getValue(Scope scope, Object self) {
		if (beanGetter != null) {
			try {
				return beanGetter.invoke(self);
			} catch (Exception ex) {
				throw new ScriptError(ex);
			}
		}

		if (field == null) {
			throw new ScriptError("No such field %s.%s".formatted(prototype, name));
		}

		try {
			return field.get(self);
		} catch (Exception ex) {
			throw new ScriptError(ex);
		}
	}

	public void setValue(Scope scope, Object self, Object value) {
		if (beanSetters != null) {
			for (var setter : beanSetters) {
				var o = scope.getContext().as(scope, value, setter.argType);

				if (o != Special.NOT_FOUND) {
					try {
						setter.method.invoke(self, o);
						return;
					} catch (Exception ex) {
						throw new ScriptError(ex);
					}
				}
			}

			return;
		}

		if (field == null) {
			throw new ScriptError("No such field %s.%s".formatted(prototype, name));
		}

		try {
			field.set(self, value);
		} catch (Exception ex) {
			throw new ScriptError(ex);
		}
	}

	public Object invoke(Scope scope, Object self, Object[] args) {
		if (methods == null) {
			throw new ScriptError("No such method %s.%s".formatted(prototype, name));
		}

		/*

		var sig = Signature.ofArgs(args);
		var m = methods.get(sig);

		if (m == null) {
			throw new IchorError("No such method %s.%s%s".formatted(prototype, name, sig));
		}

		try {
			return m.invoke(self, args);
		} catch (Exception ex) {
			throw new IchorError(ex);
		}
		 */

		throw new ScriptError("Methods aren't supported yet");
	}
}
