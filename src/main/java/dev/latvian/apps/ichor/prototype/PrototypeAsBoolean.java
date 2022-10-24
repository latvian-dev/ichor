package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;

@FunctionalInterface
public interface PrototypeAsBoolean {
	boolean asBoolean(Context cx, Object self);
}
