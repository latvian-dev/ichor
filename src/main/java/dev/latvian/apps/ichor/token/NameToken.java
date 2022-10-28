package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;

public record NameToken(String name) implements Token, Evaluable {
	@Override
	public String toString() {
		return name;
	}

	@Override
	public Object eval(Scope scope) {
		return scope.getMember(name);
	}

	@Override
	public String evalName(Scope scope) {
		return name;
	}
}
