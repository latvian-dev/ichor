package dev.latvian.apps.ichor;

public interface Evaluable {
	Object eval(Scope scope);

	default void evalString(Scope scope, StringBuilder builder) {
		var e = this.eval(scope);

		if (e == this) {
			builder.append(this);
		} else {
			scope.asString(e, builder, false);
		}
	}

	default double evalDouble(Scope scope) {
		var e = this.eval(scope);

		if (e == this) {
			return Double.NaN;
		} else {
			return scope.asDouble(e);
		}
	}

	default int evalInt(Scope scope) {
		var d = evalDouble(scope);
		return Double.isNaN(d) ? 0 : (int) d;
	}

	default boolean evalBoolean(Scope scope) {
		var d = evalDouble(scope);
		return !Double.isNaN(d) && d != 0D;
	}
}
