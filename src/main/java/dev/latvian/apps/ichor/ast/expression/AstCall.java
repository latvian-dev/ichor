package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.util.Empty;

public class AstCall extends AstExpression {
	public static Object[] convertArgs(Context cx, Scope scope, Object[] arguments) {
		if (arguments.length == 0) {
			return Empty.OBJECTS;
		}

		var args = new Object[arguments.length];

		for (int i = 0; i < arguments.length; i++) {
			args[i] = cx.eval(scope, arguments[i]);
		}

		return args;
	}

	public Object function;
	public Object[] arguments;
	public boolean isNew;

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
	public Object eval(Context cx, Scope scope) {
		var func = cx.eval(scope, function);

		if (Special.isInvalid(func)) {
			throw new ScriptError("Cannot find " + function);
		} else if (!(func instanceof Callable)) {
			throw new ScriptError("Cannot call " + function + ", " + func + " (" + cx.getPrototype(scope, func) + ")" + " is not a function");
		}

		var self = isNew ? Special.NEW : function instanceof Evaluable eval ? eval.evalSelf(cx, scope) : function;

		cx.debugger.pushSelf(cx, scope, self);

		var args = convertArgs(cx, scope, arguments);

		var r = ((Callable) func).call(cx, scope, self, args);

		if (r == Special.NOT_FOUND) {
			throw new ScriptError("Cannot call " + function);
		}

		cx.debugger.call(cx, scope, this, func, args, r);
		return r;
	}

	@Override
	public Object optimize(Parser parser) {
		function = parser.optimize(function);

		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = parser.optimize(arguments[i]);
		}

		return this;
	}
}