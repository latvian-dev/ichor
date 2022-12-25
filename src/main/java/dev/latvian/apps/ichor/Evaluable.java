package dev.latvian.apps.ichor;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface Evaluable {
	default EvaluableType getType(Scope scope) {
		return EvaluableType.UNKNOWN;
	}

	Object eval(Scope scope);

	@Nullable
	default Object evalSelf(Scope scope) {
		return null;
	}

	default void evalString(Scope scope, StringBuilder builder) {
		var e = this.eval(scope);

		if (e == this) {
			builder.append(this);
		} else {
			scope.getContext().asString(scope, e, builder);
		}
	}

	default double evalDouble(Scope scope) {
		var e = this.eval(scope);

		if (e == this) {
			return Double.NaN;
		} else {
			return scope.getContext().asDouble(scope, e);
		}
	}

	default boolean evalBoolean(Scope scope) {
		var e = this.eval(scope);

		if (e == this) {
			return true;
		} else {
			return scope.getContext().asBoolean(scope, e);
		}
	}

	default int evalInt(Scope scope) {
		var e = this.eval(scope);

		if (e == this) {
			return 1;
		} else {
			return scope.getContext().asInt(scope, e);
		}
	}

	default Evaluable optimize(Parser parser) {
		return this;
	}

	// TODO: Move this to Prototype
	default boolean equals(Scope scope, Evaluable right, boolean shallow) {
		if (this == right) {
			return true;
		} else if (shallow) {
			return eval(scope) == right.eval(scope);
		} else {
			return Objects.equals(eval(scope), right.eval(scope));
		}
	}

	// TODO: Move this to Prototype
	default int compareTo(Scope scope, Evaluable right) {
		return Double.compare(evalDouble(scope), right.evalDouble(scope));
	}
}
