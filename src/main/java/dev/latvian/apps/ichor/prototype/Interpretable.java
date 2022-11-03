package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;

public interface Interpretable {
	Interpretable[] EMPTY_INTERPRETABLE_ARRAY = new Interpretable[0];

	void interpret(Scope scope);

	default void interpretInNewScope(Scope scope) {
		try {
			interpret(scope.push());
		} finally {
			scope.pop();
		}
	}
}
