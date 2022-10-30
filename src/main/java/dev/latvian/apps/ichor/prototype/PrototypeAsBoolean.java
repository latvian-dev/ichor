package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeAsBoolean {
	boolean asBoolean(Scope scope, Object self);
}
