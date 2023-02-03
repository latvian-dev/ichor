package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.token.TokenPos;
import org.jetbrains.annotations.Nullable;

public class Special implements Token, Evaluable, PrototypeSupplier {
	public static final Object NOT_FOUND = new Object(); // Internal use only
	public static final Special NULL = new Special("null");
	public static final Special UNDEFINED = new Special("undefined");

	public static boolean isInvalid(@Nullable Object o) {
		return o == null || o instanceof Special;
	}

	public final Prototype<?> prototype;

	private Special(String name) {
		prototype = new Prototype<>(null, name, Void.TYPE);
	}

	@Override
	public Prototype<?> getPrototype(Context cx, Scope scope) {
		return prototype;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == null || obj instanceof Special;
	}

	@Override
	public String toString() {
		return prototype.getPrototypeName();
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return this == UNDEFINED ? this : null;
	}

	@Override
	public boolean evalBoolean(Context cx, Scope scope) {
		return false;
	}

	@Override
	public double evalDouble(Context cx, Scope scope) {
		return Double.NaN;
	}

	@Override
	public int evalInt(Context cx, Scope scope) {
		return 0;
	}

	@Override
	public Evaluable toEvaluable(Parser parser, TokenPos pos) {
		return this;
	}
}