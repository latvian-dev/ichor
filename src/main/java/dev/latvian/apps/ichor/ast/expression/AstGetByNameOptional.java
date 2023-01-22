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
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);
		cx.debugger.pushSelf(cx, scope, self);

		var r = p.get(cx, scope, self, name);

		if (Special.isInvalid(r)) {
			cx.debugger.get(cx, scope, this, "undefined");
			return Special.UNDEFINED;
		}

		cx.debugger.get(cx, scope, this, r);
		return r;
	}
}
