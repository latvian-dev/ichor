package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;

@FunctionalInterface
public interface PrototypeTypeAdapter<T> {
	Object adapt(Scope scope, T self, Class<?> toType);

	record Fallback<T>(PrototypeTypeAdapter<T> main, PrototypeTypeAdapter<T> fallback) implements PrototypeTypeAdapter<T> {
		@Override
		public Object adapt(Scope scope, T self, Class<?> toType) {
			var r = main.adapt(scope, self, toType);
			return r != Special.NOT_FOUND ? r : fallback.adapt(scope, self, toType);
		}
	}
}
