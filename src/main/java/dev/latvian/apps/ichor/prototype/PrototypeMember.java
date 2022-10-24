package dev.latvian.apps.ichor.prototype;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Special;
import org.jetbrains.annotations.Nullable;

public interface PrototypeMember {
	default Object get(Context cx, @Nullable Object self) {
		return Special.NOT_FOUND;
	}
}
