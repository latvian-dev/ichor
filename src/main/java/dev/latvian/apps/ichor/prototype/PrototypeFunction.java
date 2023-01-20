package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ConstructorError;

@FunctionalInterface
public interface PrototypeFunction extends PrototypeProperty {
	record CallWrapper(Object self, Scope evalScope, PrototypeFunction function) implements Callable {
		@Override
		public Object call(Context cx, Scope callScope, Object[] args, boolean hasNew) {
			if (hasNew) {
				throw new ConstructorError(null);
			}

			return function.call(cx, evalScope, self, args);
		}
	}

	Object call(Context cx, Scope scope, Object self, Object[] args);

	@Override
	default Object get(Context cx, Scope scope, Object self) {
		return new CallWrapper(self, scope, this);
	}
}
