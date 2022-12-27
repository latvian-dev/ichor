package dev.latvian.apps.ichor;

public interface Parser {
	Context getContext();

	Object expression();

	default Object optimize(Object o) {
		return o instanceof Evaluable eval ? eval.optimize(this) : o;
	}
}
