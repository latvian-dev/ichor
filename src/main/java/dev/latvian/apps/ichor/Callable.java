package dev.latvian.apps.ichor;

@FunctionalInterface
public interface Callable {
	Object call(Scope scope, Object self, Object[] args);
}