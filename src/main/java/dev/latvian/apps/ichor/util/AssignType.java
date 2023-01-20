package dev.latvian.apps.ichor.util;

public enum AssignType {
	NONE,
	MUTABLE,
	IMMUTABLE;

	public boolean isSet() {
		return this != NONE;
	}

	public boolean isMutable() {
		return this == MUTABLE;
	}

	public boolean isImmutable() {
		return this == IMMUTABLE;
	}
}
