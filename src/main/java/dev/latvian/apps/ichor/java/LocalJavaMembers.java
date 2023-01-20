package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.InternalScriptError;
import dev.latvian.apps.ichor.prototype.PrototypeProperty;
import dev.latvian.apps.ichor.util.Empty;
import org.jetbrains.annotations.Nullable;

public record LocalJavaMembers(JavaMembers members) implements PrototypeProperty {
	public record CallWrapper(Object self, JavaMembers members) implements Callable {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return members.call(cx, scope, args, self);
		}
	}

	@Override
	public Object get(Context cx, Scope scope, Object self) {
		try {
			if (members.beanGet != null) {
				return members.beanGet.invoke(self, Empty.OBJECTS);
			}

			if (members.field != null) {
				return members.field.get(self);
			}
		} catch (Exception ex) {
			throw new InternalScriptError(ex);
		}

		if (members.methods != null) {
			return new CallWrapper(self, members);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Context cx, Scope scope, Object self, @Nullable Object value) {
		try {
			if (members.beanSet != null) {
				members.beanSet.invoke(self, cx.as(scope, value, members.beanSetType));
				return true;
			}

			if (members.field != null) {
				members.field.set(self, cx.as(scope, value, members.field.getType()));
				return true;
			}
		} catch (Exception ex) {
			throw new InternalScriptError(ex);
		}

		return false;
	}
}
