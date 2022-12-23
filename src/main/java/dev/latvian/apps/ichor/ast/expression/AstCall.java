package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.util.Empty;

public class AstCall extends AstExpression {
	public static Object[] convertArgs(Scope scope, Evaluable[] arguments) {
		if (arguments.length == 0) {
			return Empty.OBJECTS;
		}

		var args = new Object[arguments.length];

		for (int i = 0; i < arguments.length; i++) {
			args[i] = arguments[i].eval(scope);
		}

		return args;
	}

	public final Evaluable function;
	public final Evaluable[] arguments;
	public final boolean isNew;

	public AstCall(Evaluable function, Evaluable[] arguments, boolean isNew) {
		this.function = function;
		this.arguments = arguments;
		this.isNew = isNew;
	}

	@Override
	public void append(AstStringBuilder builder) {
		if (isNew) {
			builder.append("new ");
		}

		builder.append(function);
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
		var func = function.eval(scope);

		if (Special.isInvalid(func)) {
			throw new ScriptError("Cannot find " + function);
		} else if (!(func instanceof Callable)) {
			throw new ScriptError("Cannot call " + function + ", " + scope.getContext().asString(scope, func) + " (" + scope.getContext().getPrototype(func) + ")" + " is not a function");
		}

		var self = isNew ? Special.NEW : function.evalSelf(scope);

		var cx = scope.getContext();
		cx.debugger.pushSelf(scope, self);

		var r = ((Callable) func).call(scope, self, convertArgs(scope, arguments));

		if (r == Special.NOT_FOUND) {
			throw new ScriptError("Cannot call " + function);
		}

		cx.debugger.call(scope, this, r);
		return r;
	}
}