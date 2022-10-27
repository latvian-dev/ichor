package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.error.IchorError;

public interface Token {
	default boolean isPrimary() {
		return false;
	}

	default Object getPrimaryValue() {
		throw new IchorError("Not a primary token!");
	}
}
