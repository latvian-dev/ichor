package dev.latvian.apps.ichor;

public interface Callable {
	default Object call(Scope scope, Object[] args) {
		return Special.NOT_FOUND;
	}

	default Object construct(Scope scope, Object[] args) {
		return Special.NOT_FOUND;
	}
}
