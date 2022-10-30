package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import org.jetbrains.annotations.Nullable;

public interface PrototypeMember {
	default Object get(Scope scope, @Nullable Object self) {
		return Special.NOT_FOUND;
	}
}
