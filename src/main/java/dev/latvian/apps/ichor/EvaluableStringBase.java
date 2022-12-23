package dev.latvian.apps.ichor;

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
	default boolean equals(Object right, Scope scope, boolean shallow) {
		return eval(scope).equals(String.valueOf(right));
	}
}
