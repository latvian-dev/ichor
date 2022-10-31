package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;

public class BooleanToken implements Token, Evaluable {
	public static final BooleanToken TRUE = new BooleanToken(true);
	public static final BooleanToken FALSE = new BooleanToken(false);

	public final boolean value;
	public final Boolean objectValue;

	private BooleanToken(boolean v) {
		value = v;
		objectValue = v;
	}

	@Override
	public String toString() {
		return value ? "true" : "false";
	}

	@Override
	public Object eval(Scope scope) {
		return objectValue;
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return value;
	}

	@Override
	public double evalDouble(Scope scope) {
		return value ? 1D : 0D;
	}

	@Override
	public boolean hasValue() {
		return true;
	}

	@Override
	public Object getValue() {
		return objectValue;
	}
}
