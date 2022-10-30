package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import dev.latvian.apps.ichor.token.StaticToken;
import org.jetbrains.annotations.Nullable;

public class Special implements PrototypeSupplier, StaticToken, Evaluable {
	public static final Special NOT_FOUND = new Special("<not found>"); // Internal use only
	public static final Special NULL = new Special("null");
	public static final Special UNDEFINED = new Special("undefined");

	public static boolean isInvalid(@Nullable Object o) {
		return o == null || o == NOT_FOUND || o == NULL || o == UNDEFINED;
	}

	private final Prototype prototype;

	private Special(String name) {
		prototype = PrototypeBuilder.create(name);
	}

	@Override
	public Prototype getPrototype() {
		return prototype;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Special;
	}

	@Override
	public String toString() {
		return prototype.getPrototypeName();
	}

	@Override
	public Object eval(Scope scope) {
		return null;
	}
}