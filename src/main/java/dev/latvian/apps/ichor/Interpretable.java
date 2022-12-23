package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.IchorError;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.exit.ScopeExit;

public interface Interpretable {
	Interpretable[] EMPTY_INTERPRETABLE_ARRAY = new Interpretable[0];

	void interpret(Scope scope);

	default void interpretSafe(Scope scope) {
		try {
			interpret(scope);
		} catch (IchorError | ScopeExit pass) {
			throw pass;
		} catch (Throwable ex) {
			throw new ScriptError("Internal error", ex).pos(this);
		}
	}
}
