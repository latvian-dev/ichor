package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeAsBoolean {
	boolean asBoolean(Context cx, Scope scope, Object self);
}
