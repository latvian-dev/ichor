package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import dev.latvian.apps.ichor.token.Token;
import dev.latvian.apps.ichor.token.TokenPos;
import org.jetbrains.annotations.Nullable;

public class Special implements PrototypeSupplier, Token {
	public static final Special NOT_FOUND = new Special("<not found>"); // Internal use only
	public static final Special NULL = new Special("null");
	public static final Special UNDEFINED = new Special("undefined");
	public static final Special IMMUTABLE = new Special("<immutable>");
	public static final Special NEW = new Special("<new>");

	public static boolean isInvalid(@Nullable Object o) {
		return o == null || o instanceof Special;
	}

	private final Prototype prototype;

	private Special(String name) {
		prototype = new PrototypeBuilder(name);
	}

	@Override
	public Prototype getPrototype(Context cx) {
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
	public Object eval(Scope scope) {
		return this == UNDEFINED ? this : null;
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return false;
	}

	@Override
	public double evalDouble(Scope scope) {
		return Double.NaN;
	}

	@Override
	public int evalInt(Scope scope) {
		return 0;
	}

	@Override
	public boolean equals(Scope scope, Evaluable right, boolean shallow) {
		var r = right.eval(scope);
		return r == null || r instanceof Special;
	}

	@Override
	public Evaluable toEvaluable(Parser parser, TokenPos pos) {
		return this;
	}
}