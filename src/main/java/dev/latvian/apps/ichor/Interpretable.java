package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.exit.ScopeExit;

public interface Interpretable {
	Interpretable[] EMPTY_INTERPRETABLE_ARRAY = new Interpretable[0];

	void interpret(Scope scope) throws ScopeExit;
}
