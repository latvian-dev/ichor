package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.token.StringToken;

public interface EvaluableStringBase extends Evaluable {
	@Override
	void evalString(Scope scope, StringBuilder builder);

	@Override
	default String eval(Scope scope) {
		var builder = new StringBuilder();
		evalString(scope, builder);
		return builder.toString();
	}

	@Override
	default double evalDouble(Scope scope) {
		try {
			return Double.parseDouble(eval(scope));
		} catch (NumberFormatException ex) {
			return Double.NaN;
		}
	}

	@Override
	default int evalInt(Scope scope) {
		try {
			return (int) Double.parseDouble(eval(scope));
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	@Override
	default boolean evalBoolean(Scope scope) {
		return !eval(scope).isEmpty();
	}

	@Override
	default boolean equals(Scope scope, Evaluable right, boolean shallow) {
		var left = eval(scope);

		if (right instanceof StringToken string) {
			return left.equals(string.value);
		} else {
			var builder = new StringBuilder();
			right.evalString(scope, builder);
			return left.equals(builder.toString());
		}
	}
}
