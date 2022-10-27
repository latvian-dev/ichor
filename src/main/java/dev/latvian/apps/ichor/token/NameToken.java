package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Frame;

public record NameToken(String name) implements Token, Evaluable {
	@Override
	public String toString() {
		return name;
	}

	@Override
	public Object eval(Frame frame) {
		return frame.getScope().getMember(name);
	}

	@Override
	public String evalName(Frame frame) {
		return name;
	}
}
