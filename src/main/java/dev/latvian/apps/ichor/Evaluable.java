package dev.latvian.apps.ichor;

import java.util.Objects;

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

	default Evaluable optimize() {
		return this;
	}

	default boolean equals(Object right, Scope scope, boolean shallow) {
		if (this == right) {
			return true;
		} else if (shallow) {
			return eval(scope) == right;
		} else {
			return Objects.equals(eval(scope), right);
		}
	}

	default int compareTo(Object right, Scope scope) {
		return Double.compare(evalDouble(scope), right instanceof Number n ? n.doubleValue() : Double.NaN);
	}
}
