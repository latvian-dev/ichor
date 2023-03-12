package dev.latvian.apps.ichor;

public interface Optimizable {
	default Object optimize(Parser parser) {
		return this;
	}
}
