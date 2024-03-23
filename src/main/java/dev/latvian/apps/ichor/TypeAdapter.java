package dev.latvian.apps.ichor;

public interface TypeAdapter {
	default <T> boolean canAdapt(Scope scope, Class<T> type) {
		if (type != null && type.isInterface()) {
			return scope.getClassPrototype(type).isSingleMethodInterface();
		}

		return false;
	}

	<T> T adapt(Scope scope, Class<T> type);
}
