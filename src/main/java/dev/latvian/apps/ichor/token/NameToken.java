package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Evaluable;

public record NameToken(String name) implements Token, Evaluable {
	@Override
	public String toString() {
		return name;
	}

	@Override
	public Object eval(Scope scope) {
		var r = scope.getMember(name);

		if (r == Special.NOT_FOUND) {
			throw new ScriptError("Cannot find " + name);
		}

		return r;
	}
}
