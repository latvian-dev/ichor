package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.IchorError;

public interface Evaluable {
	Object eval(Frame frame);

	default double evalNumber(Frame frame) {
		return frame.getContext().asDouble(eval(frame));
	}

	default boolean evalBoolean(Frame frame) {
		return frame.getContext().asBoolean(eval(frame));
	}

	default String evalName(Frame frame) {
		throw new IchorError("Expected name!");
	}
}
