package dev.latvian.apps.ichor.util;

public enum AssignType {
	NONE,
	MUTABLE,
	IMMUTABLE;

	public boolean isImmutable() {
		return this == IMMUTABLE;
	}
}
