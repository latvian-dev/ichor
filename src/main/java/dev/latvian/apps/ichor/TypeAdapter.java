package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.java.JavaClassPrototype;

public interface TypeAdapter {
	default <T> boolean canAdapt(Context cx, Class<T> type) {
		if (type != null && type.isInterface()) {
			var proto = cx.getClassPrototype(type);
			return proto instanceof JavaClassPrototype p ? p.isSingleMethodInterface() : JavaClassPrototype.isSingleMethodInterface(type);
		}

		return false;
	}

	<T> T adapt(Context cx, Scope scope, Class<T> type);
}
