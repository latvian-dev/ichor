package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstGetByNameOptional extends AstGetByName {
	public AstGetByNameOptional(Object from, String name) {
		super(from, name);
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);
		builder.append("?.");
		builder.append(name);
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		var r = cx.wrap(scope, evalSelf(cx, scope)).get(cx, scope, name);

		if (Special.isInvalid(r)) {
			return Special.UNDEFINED;
		}

		return r;
	}
}
