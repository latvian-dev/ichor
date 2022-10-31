package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.error.ScriptError;

public interface Token {
	default boolean hasValue() {
		return false;
	}

	default Object getValue() {
		throw new ScriptError(this + " is not a primary token");
	}
}
