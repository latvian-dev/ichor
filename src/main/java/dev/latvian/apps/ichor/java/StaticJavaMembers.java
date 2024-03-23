package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.CallableTypeAdapter;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.InternalScriptError;
import dev.latvian.apps.ichor.prototype.PrototypeStaticProperty;
import dev.latvian.apps.ichor.util.Empty;
import org.jetbrains.annotations.Nullable;

public record StaticJavaMembers(JavaMembers members) implements PrototypeStaticProperty {
	public record CallWrapper(Scope evalScope, JavaMembers members) implements CallableTypeAdapter {
		@Override
		public Object call(Scope scope, Object[] args, boolean hasNew) {
			return members.call(scope, args, null);
		}

		@Override
		public Scope getEvalScope() {
			return evalScope;
		}

		@Override
		public String toString() {
			return "StaticProxy[" + members.prototype + "]";
		}

		@Override
		public int hashCode() {
			return members.prototype.hashCode();
		}
	}

	@Override
	public Object get(Scope scope) {
		try {
			if (members.beanGet != null) {
				return members.beanGet.invoke(null, Empty.OBJECTS);
			}

			if (members.field != null) {
				return members.field.get(null);
			}
		} catch (Exception ex) {
			throw new InternalScriptError(ex);
		}

		if (members.methods != null) {
			return new CallWrapper(scope, members);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Scope scope, @Nullable Object value) {
		try {
			if (members.beanSet != null) {
				members.beanSet.invoke(null, scope.as(value, members.beanSetType));
				return true;
			}

			if (members.field != null) {
				members.field.set(null, scope.as(value, members.field.getType()));
				return true;
			}
		} catch (Exception ex) {
			throw new InternalScriptError(ex);
		}

		return false;
	}
}
