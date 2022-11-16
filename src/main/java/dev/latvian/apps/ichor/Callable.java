package dev.latvian.apps.ichor;

@FunctionalInterface
public interface Callable {
	Object call(Scope scope, Object self, Evaluable[] args);

	default Object construct(Scope scope, Evaluable[] args) {
		return Special.NOT_FOUND;
	}
}
