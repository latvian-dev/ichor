package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;

@FunctionalInterface
public interface PrototypeAsNumber {
	Number asNumber(Context cx, Object self);
}
