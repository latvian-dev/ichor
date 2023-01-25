package dev.latvian.apps.ichor;

@FunctionalInterface
public interface WrappedObjectFactory {
	WrappedObject create(Context cx, Scope scope);
}
