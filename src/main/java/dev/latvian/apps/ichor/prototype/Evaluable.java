package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;

public interface Evaluable {
	Evaluable UNIMPL = new Evaluable() {
		@Override
		public Object eval(Scope scope) {
			throw new ScriptError("This property is not yet implemented");
		}

		@Override
		public String toString() {
			return "<unimplemented>";
		}
	};

	Object eval(Scope scope);

	default String evalString(Scope scope) {
		return scope.getContext().asString(scope, eval(scope));
	}

	default double evalDouble(Scope scope) {
		return scope.getContext().asDouble(scope, eval(scope));
	}

	default boolean evalBoolean(Scope scope) {
		return scope.getContext().asBoolean(scope, eval(scope));
	}

	default int evalInt(Scope scope) {
		return scope.getContext().asInt(scope, eval(scope));
	}

	default Object optimize() {
		return this;
	}
}
