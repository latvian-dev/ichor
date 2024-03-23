package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;

public record EvaluableConstant(Object value) implements Evaluable {
	@Override
	public Object eval(Scope scope) {
		return value == Special.NULL ? null : value;
	}
}
