package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Callable;

public class AstCall extends AstExpression {
	public final Object callee;
	public final Object[] arguments;

	public AstCall(Object callee, Object[] arguments) {
		this.callee = callee;
		this.arguments = arguments;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(callee);
		builder.append('(');

		for (int i = 0; i < arguments.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			builder.append(arguments[i]);
		}

		builder.append(')');
	}

	@Override
	public Object eval(Scope scope) {
		var cx = scope.getContext();
		var self = scope.eval(callee);
		var c = self instanceof Callable c1 ? c1 : cx.getPrototype(self);

		if (cx.debugger != null) {
			cx.debugger.pushSelf(self);
		}

		var r = c.call(scope, self, arguments);

		if (r == Special.NOT_FOUND) {
			throw new RuntimeException("Cannot call " + this);
		}

		if (cx.debugger != null) {
			cx.debugger.call(callee, arguments, r);
		}

		return r;
	}
}
