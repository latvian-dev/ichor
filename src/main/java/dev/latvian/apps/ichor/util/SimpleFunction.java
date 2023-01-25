package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

public record SimpleFunction<T>(T self, SimpleFunction.Callback<T> function) implements Callable {
	@FunctionalInterface
	public interface Callback<T> {
		Object call(Context cx, Scope scope, T self, Object[] args);
	}

	public static <T> SimpleFunction<T> of(T self, Callback<T> function) {
		return new SimpleFunction<>(self, function);
	}

	@Override
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		return function.call(cx, scope, self, args);
	}
}
