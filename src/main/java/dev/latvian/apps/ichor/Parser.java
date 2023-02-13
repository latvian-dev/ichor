package dev.latvian.apps.ichor;

public interface Parser {
	Context getContext();

	default RootScope getRootScope() {
		throw new IllegalStateException("This parser doesn't have a root scope");
	}

	default Interpretable parse() {
		throw new IllegalStateException("This parser doesn't support interpreting");
	}

	default Object expression() {
		throw new IllegalStateException("This parser doesn't support evaluating");
	}

	default Object optimize(Object o) {
		return o instanceof Evaluable eval ? eval.optimize(this) : o;
	}
}
