package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

public class AstNew extends AstExpression {
	public final Evaluable callee;
	public final Evaluable[] arguments;

	public AstNew(Evaluable callee, Evaluable[] arguments) {
		this.callee = callee;
		this.arguments = arguments;
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

		var r = ((Callable) o).construct(scope, arguments);

		if (r == Special.NOT_FOUND) {
			throw new ScriptError("Cannot construct " + o);
		}

		return r;
	}
}
