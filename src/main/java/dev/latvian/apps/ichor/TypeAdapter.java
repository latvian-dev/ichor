package dev.latvian.apps.ichor;

public interface TypeAdapter {
	default <T> boolean canAdapt(Context cx, Class<T> type) {
		if (type != null && type.isInterface()) {
			return cx.getClassPrototype(type).isSingleMethodInterface();
		}

		return false;
	}

	<T> T adapt(Context cx, Scope scope, Class<T> type);
}
