package dev.latvian.apps.ichor;

public interface Evaluable {
	Object eval(Scope scope);

	default String evalString(Scope scope) {
		return String.valueOf(eval(scope));
	}

	default double evalDouble(Scope scope) {
		return eval(scope) instanceof Number n ? n.doubleValue() : Double.NaN;
	}

	default boolean evalBoolean(Scope scope) {
		return eval(scope) instanceof Boolean b ? b : true;
	}

	default int evalInt(Scope scope) {
		return eval(scope) instanceof Number n ? n.intValue() : -1;
	}

	default Object optimize() {
		return this;
	}
}
