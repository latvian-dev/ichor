package dev.latvian.apps.ichor;

public interface EvaluableStringBase extends Evaluable {
	@Override
	void evalString(Context cx, Scope scope, StringBuilder builder);

	@Override
	default String eval(Context cx, Scope scope) {
		var builder = new StringBuilder();
		evalString(cx, scope, builder);
		return builder.toString();
	}

	@Override
	default double evalDouble(Context cx, Scope scope) {
		try {
			return Double.parseDouble(eval(cx, scope));
		} catch (NumberFormatException ex) {
			return Double.NaN;
		}
	}

	@Override
	default int evalInt(Context cx, Scope scope) {
		try {
			return (int) Double.parseDouble(eval(cx, scope));
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	@Override
	default boolean evalBoolean(Context cx, Scope scope) {
		return !eval(cx, scope).isEmpty();
	}
}
