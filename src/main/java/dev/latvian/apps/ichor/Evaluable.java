package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.ScriptError;

public interface Evaluable {
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

	default String evalName(Scope scope) {
		throw new ScriptError("Expected name!");
	}

	default Object optimize() {
		return this;
	}
}
