package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;

@FunctionalInterface
public interface PrototypeAsNumber {
	Number asNumber(Scope scope, Object self);
}
