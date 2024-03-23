package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.expression.AstCall;
import dev.latvian.apps.ichor.error.InternalScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Empty;
import dev.latvian.apps.ichor.util.Signature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public final class JavaMembers {
	public record SigMethod(Signature signature, Method method) implements Comparable<SigMethod> {
		@Override
		public int compareTo(@NotNull SigMethod o) {
			return Integer.compare(o.signature.types.length, signature.types.length);
		}
	}

	public final String name;
	public Prototype prototype;
	public Field field;
	public SigMethod[] methods;
	public Method beanGet;
	public Method beanSet;
	public Class<?> beanSetType;

	public JavaMembers(String name) {
		this.name = name;
	}

	public void prepare(Prototype p) {
		prototype = p;

		if (methods != null && methods.length >= 2) {
			Arrays.sort(methods, null);
		}
	}

	public void addFieldAccessors(Map<String, JavaMembers> otherMembers) {
		if (field != null && (beanGet != null || beanSet != null)) {
			otherMembers.computeIfAbsent("__" + name, JavaMembers::new).addField(field);
		}
	}

	public void addField(Field f) {
		field = f;
	}

	@Override
	public String toString() {
		return name;
	}

	public void addMethod(Method m, Signature signature, Map<String, JavaMembers> otherMembers) {
		if (methods == null) {
			methods = new SigMethod[1];
		} else {
			var methods1 = new SigMethod[methods.length + 1];
			System.arraycopy(methods, 0, methods1, 0, methods.length);
			methods = methods1;
		}

		methods[methods.length - 1] = new SigMethod(signature, m);

		char[] c = name.toCharArray();

		if (c.length >= 3 && c[0] == 'i' && c[1] == 's' && c[2] >= 'A' && c[2] <= 'Z') {
			c[2] = Character.toLowerCase(c[2]);

			if (m.getParameterCount() == 0 && (m.getReturnType() == Boolean.class || m.getReturnType() == Boolean.TYPE)) {
				var members = otherMembers.computeIfAbsent(new String(c, 2, c.length - 2), JavaMembers::new);
				members.beanGet = m;
			}
		} else if (c.length >= 4 && (c[0] == 'g' || c[0] == 's') && c[1] == 'e' && c[2] == 't' && c[3] >= 'A' && c[3] <= 'Z') {
			c[3] = Character.toLowerCase(c[3]);

			if (c[0] == 'g' ? (m.getParameterCount() == 0 && m.getReturnType() != Void.TYPE) : (m.getParameterCount() == 1)) {
				var members = otherMembers.computeIfAbsent(new String(c, 3, c.length - 3), JavaMembers::new);

				if (c[0] == 'g') {
					members.beanGet = m;
				} else {
					members.beanSet = m;
					members.beanSetType = m.getParameterTypes()[0];
				}
			}
		}
	}

	public Object call(Scope scope, Object[] args, @Nullable Object self) {
		if (methods == null) {
			throw new AstCall.CallError(this, this, prototype);
		}

		// System.out.println("Calling method " + name + " with " + args.length + " args " + Arrays.toString(args));

		try {
			for (var m : methods) {
				// FIXME: verify argument match

				if (args.length >= m.signature.types.length) {
					if (m.signature.types.length > 0) {
						var args1 = new Object[m.signature.types.length];

						for (int i = 0; i < args1.length; i++) {
							args1[i] = scope.as(args[i], m.signature.types[i]);
						}

						return m.method.invoke(self, args1);
					} else {
						return m.method.invoke(self, Empty.OBJECTS);
					}
				}
			}
		} catch (Exception ex) {
			throw new InternalScriptError(ex);
		}

		throw new AstCall.CallError(this, this, prototype);
	}
}
