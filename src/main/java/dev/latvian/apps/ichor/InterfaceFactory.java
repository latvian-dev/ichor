package dev.latvian.apps.ichor;

@FunctionalInterface
public interface InterfaceFactory {
	<T> T createInterface(Context cx, Class<T> type);
}
