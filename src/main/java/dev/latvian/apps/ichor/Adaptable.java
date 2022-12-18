package dev.latvian.apps.ichor;

@FunctionalInterface
public interface Adaptable {
	<T> T adapt(Context cx, Class<T> type);
}
