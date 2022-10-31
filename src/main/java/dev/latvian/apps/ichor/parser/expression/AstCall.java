package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstCall extends AstExpression {
	public final Evaluable callee;
	public final Object[] arguments;
	private boolean needEval;

	public AstCall(Evaluable callee, Object[] arguments) {
		this.callee = callee;
		this.arguments = arguments;

		if (this.arguments.length > 0) {
			for (var o : this.arguments) {
				if (o instanceof Evaluable) {
					needEval = true;
					break;
				}
			}
		}
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
		var o = callee.eval(scope);
		var p = scope.getContext().getPrototype(o);

		if (needEval) {
			var args = new Object[arguments.length];

			for (int i = 0; i < arguments.length; i++) {
				if (arguments[i] instanceof Evaluable evaluable) {
					args[i] = evaluable.eval(scope);
				} else {
					args[i] = arguments[i];
				}
			}

			return p.call(scope, args, o);
		} else {
			return p.call(scope, arguments, o);
		}
	}
}
