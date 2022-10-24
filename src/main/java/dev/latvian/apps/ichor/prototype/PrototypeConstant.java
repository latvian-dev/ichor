package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import org.jetbrains.annotations.Nullable;

public record PrototypeConstant(Object value) implements PrototypeMember {
	@Override
	public Object get(Context cx, @Nullable Object self) {
		return value;
	}
}
