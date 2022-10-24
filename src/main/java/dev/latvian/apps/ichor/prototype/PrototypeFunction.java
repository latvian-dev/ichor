package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;

@FunctionalInterface
public interface PrototypeFunction extends PrototypeMember {
	Object call(Context cx, Object self, Object[] args);
}
