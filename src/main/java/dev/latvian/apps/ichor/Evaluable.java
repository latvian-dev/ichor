package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.ScriptError;

public interface Evaluable {
	Object eval(Scope scope);

	default double evalNumber(Scope scope) {
		return scope.root.context.asDouble(eval(scope));
	}

	default boolean evalBoolean(Scope scope) {
		return scope.root.context.asBoolean(eval(scope));
	}

	default String evalName(Scope scope) {
		throw new ScriptError("Expected name!");
	}
}
