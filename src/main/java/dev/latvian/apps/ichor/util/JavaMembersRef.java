package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Ref;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.IchorError;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class JavaMembersRef implements Ref {
	private final Class<?> type;
	private final String name;
	public Field field;
	public Map<Signature, Method> methods;
	public Method beanGetter;
	public Map<Class<?>, Method> beanSetters;
	private Map<Class<?>, Method> beanSetterCache;

	public JavaMembersRef(Class<?> t, String n) {
		type = t;
		name = n;
	}

	@Override
	public Object getValue(Context cx, Scope scope, Object self) {
		if (beanGetter != null) {
			try {
				return beanGetter.invoke(self);
			} catch (Exception ex) {
				throw new IchorError(ex);
			}
		}

		if (field == null) {
			throw new IchorError("No such field %s.%s".formatted(type.getName(), name));
		}

		try {
			return field.get(self);
		} catch (Exception ex) {
			throw new IchorError(ex);
		}
	}

	@Override
	public void setValue(Context cx, Scope scope, Object self, Object value) {
		if (beanSetters != null) {
			try {
				// beanSetter.invoke(self, value);
			} catch (Exception ex) {
				throw new IchorError(ex);
			}
			return;
		}

		if (field == null) {
			throw new IchorError("No such field %s.%s".formatted(type.getName(), name));
		}

		try {
			field.set(self, value);
		} catch (Exception ex) {
			throw new IchorError(ex);
		}
	}
}
