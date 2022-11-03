package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Callable;
import dev.latvian.apps.ichor.prototype.Evaluable;

public class AstNew extends AstExpression {
	public final Evaluable callee;
	public final Object[] arguments;
	private boolean needEval;

	public AstNew(Evaluable callee, Object[] arguments) {
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
		builder.append("new ");
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

		if (!(o instanceof Callable)) {
			throw new ScriptError("Cannot construct " + o);
		}

		Object r;

		if (needEval) {
			var args = new Object[arguments.length];

			for (int i = 0; i < arguments.length; i++) {
				args[i] = scope.eval(arguments[i]);
			}

			r = ((Callable) o).construct(scope, args);
		} else {
			r = ((Callable) o).construct(scope, arguments);
		}

		if (r == Special.NOT_FOUND) {
			throw new ScriptError("Cannot construct " + o);
		}

		return r;
	}
}
