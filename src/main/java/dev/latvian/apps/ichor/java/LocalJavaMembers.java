package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.CallableTypeAdapter;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.InternalScriptError;
import dev.latvian.apps.ichor.prototype.PrototypeProperty;
import dev.latvian.apps.ichor.util.Empty;
import org.jetbrains.annotations.Nullable;

public record LocalJavaMembers(JavaMembers members) implements PrototypeProperty {
	public record CallWrapper(Scope evalScope, Object self, JavaMembers members) implements CallableTypeAdapter {
		@Override
		public Object call(Scope scope, Object[] args, boolean hasNew) {
			return members.call(scope, args, self);
		}

		@Override
		public Scope getEvalScope() {
			return evalScope;
		}

		@Override
		public String toString() {
			return "Proxy[" + self + "]";
		}

		@Override
		public int hashCode() {
			return self.hashCode();
		}
	}

	@Override
	public Object get(Scope scope, Object self) {
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
			return new CallWrapper(scope, self, members);
		}

		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Scope scope, Object self, @Nullable Object value) {
		try {
			if (members.beanSet != null) {
				members.beanSet.invoke(self, scope.as(value, members.beanSetType));
				return true;
			}

			if (members.field != null) {
				members.field.set(self, scope.as(value, members.field.getType()));
				return true;
			}
		} catch (Exception ex) {
			throw new InternalScriptError(ex);
		}

		return false;
	}
}
