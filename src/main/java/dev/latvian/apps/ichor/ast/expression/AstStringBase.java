package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;

public abstract class AstStringBase extends AstExpression {
	@Override
	public abstract String evalString(Scope scope);

	@Override
	public String eval(Scope scope) {
		return evalString(scope);
	}

	@Override
	public double evalDouble(Scope scope) {
		try {
			return Double.parseDouble(evalString(scope));
		} catch (NumberFormatException ex) {
			return Double.NaN;
		}
	}

	@Override
	public int evalInt(Scope scope) {
		try {
			return (int) Double.parseDouble(evalString(scope));
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return !evalString(scope).isEmpty();
	}

	@Override
	public boolean equals(Object right, Scope scope, boolean shallow) {
		return evalString(scope).equals(String.valueOf(right));
	}
}
