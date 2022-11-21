package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstAppendable;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class BooleanToken implements Token, Evaluable, AstAppendable {
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
	public Evaluable toEvaluable(Parser parser) {
		return this;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.builder.append(value);
	}

	@Override
	public boolean equals(Evaluable right, Scope scope, boolean shallow) {
		return value == right.evalBoolean(scope);
	}

	@Override
	public int compareTo(Evaluable right, Scope scope) {
		return Boolean.compare(value, right.evalBoolean(scope));
	}
}
