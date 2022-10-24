package dev.latvian.apps.ichor;

public interface Callable {
	default Object call(Context cx, Object[] args) {
		return Special.NOT_FOUND;
	}

	default Object construct(Context cx, Object[] args) {
		return Special.NOT_FOUND;
	}
}
