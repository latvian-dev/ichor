package dev.latvian.apps.ichor;

@FunctionalInterface
public interface Callable {
	Object call(Context cx, Scope scope, Object self, Object[] args);
}