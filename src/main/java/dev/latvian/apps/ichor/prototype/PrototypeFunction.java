package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeFunction extends PrototypeMember {
	Object call(Scope scope, Object self, Object[] args);
}
