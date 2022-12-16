package dev.latvian.apps.ichor;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Callable {
	Object call(Scope scope, Object self, Evaluable[] args);

	default Object construct(Scope scope, Evaluable[] args) {
		return Special.NOT_FOUND;
	}

	@Nullable
	default <T> T adapt(Class<T> type) {
		return null;
	}
}