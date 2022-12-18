package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

public record SuperInstance(Scope evalScope) implements Prototype {
	@Override
	public String getPrototypeName() {
		return "super";
	}

	@Override
	@Nullable
	public Object get(Scope scope, Object self, String name) {
		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Scope scope, Object self, String name, @Nullable Object value) {
		return false;
	}
}
