package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

public record PrototypeConstant(Object value) implements PrototypeStaticProperty {
	@Override
	public Object get(Context cx, Scope scope) {
		return value;
	}
}
