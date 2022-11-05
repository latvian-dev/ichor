package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

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
		var cx = scope.getContext();
		var self = scope.eval(from);
		var p = cx.getPrototype(self);

		if (cx.debugger != null) {
			cx.debugger.pushSelf(scope, self);
		}

		var r = p.get(scope, self, name);

		if (Special.isInvalid(r)) {
			if (cx.debugger != null) {
				cx.debugger.get(scope, this, "undefined");
			}

			return Special.UNDEFINED;
		}

		if (cx.debugger != null) {
			cx.debugger.get(scope, this, r);
		}

		return r;
	}
}
