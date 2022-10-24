package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PrototypeProperty extends PrototypeMember {
	@Override
	Object get(Context cx, @Nullable Object self);
}
