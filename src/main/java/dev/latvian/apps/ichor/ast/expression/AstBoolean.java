package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstBoolean extends AstExpression {
	public final boolean value;
	public final Boolean valueObj;

	public AstBoolean(boolean v) {
		value = v;
		valueObj = v;
	}

	@Override
	public Object eval(Scope scope) {
		return valueObj;
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return value;
	}

	@Override
	public String evalString(Scope scope) {
		return value ? "true" : "false";
	}

	@Override
	public double evalDouble(Scope scope) {
		return value ? 1D : 0D;
	}

	@Override
	public int evalInt(Scope scope) {
		return value ? 1 : 0;
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