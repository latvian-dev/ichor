package dev.latvian.apps.ichor;

public interface Evaluable {
	Object eval(Context cx, Scope scope);

	default void evalString(Context cx, Scope scope, StringBuilder builder) {
		var e = this.eval(cx, scope);

		if (e == this) {
			builder.append(this);
		} else {
			cx.asString(scope, e, builder, false);
		}
	}

	default double evalDouble(Context cx, Scope scope) {
		var e = this.eval(cx, scope);

		if (e == this) {
			return Double.NaN;
		} else {
			return cx.asDouble(scope, e);
		}
	}

	default int evalInt(Context cx, Scope scope) {
		var d = evalDouble(cx, scope);
		return Double.isNaN(d) ? 0 : (int) d;
	}

	default boolean evalBoolean(Context cx, Scope scope) {
		var d = evalDouble(cx, scope);
		return !Double.isNaN(d) && d != 0D;
	}
}
