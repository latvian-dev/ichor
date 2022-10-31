package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstGetByNameOptional extends AstGetByName {
	public AstGetByNameOptional(Evaluable from, String name) {
		super(from, name);
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(from);
		builder.append("?.");
		builder.append(name);
	}

	@Override
	public Object eval(Scope scope) {
		var o = from.eval(scope);
		var p = scope.getContext().getPrototype(o);
		return p.has(scope, name, o) ? p.get(scope, name, o) : Special.UNDEFINED;
	}
}
