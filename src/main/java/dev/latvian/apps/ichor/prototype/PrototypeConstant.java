package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

public record PrototypeConstant(Object value) implements PrototypeProperty {
	@Override
	public Object get(Context cx, Scope scope, Object self) {
		return value;
	}
}
