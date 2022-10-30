package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstCall extends AstExpression {
	public final Evaluable callee;
	public final Object[] arguments;
	private boolean needEvaluate;

	public AstCall(Evaluable callee, Object[] arguments) {
		this.callee = callee;
		this.arguments = arguments;

		if (this.arguments.length > 0) {
			for (var o : this.arguments) {
				if (o instanceof Evaluable) {
					needEvaluate = true;
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
		if (callee.eval(scope) instanceof Callable callable) {
			if (needEvaluate) {
				var args = new Object[arguments.length];

				for (int i = 0; i < arguments.length; i++) {
					if (arguments[i] instanceof Evaluable evaluable) {
						args[i] = evaluable.eval(scope);
					} else {
						args[i] = arguments[i];
					}
				}

				return callable.call(scope, args);
			} else {
				return callable.call(scope, arguments);
			}
		}

		throw new ScriptError(callee + " is not a function!");
	}
}
