package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PrototypeProperty extends PrototypeMember {
	@Override
	Object get(Scope scope, @Nullable Object self);
}
