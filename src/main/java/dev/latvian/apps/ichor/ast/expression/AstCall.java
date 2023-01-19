package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;

public class AstCall extends AstExpression {
	public static class FunctionNotFoundError extends ScriptError {
		public final Object function;

		public FunctionNotFoundError(Object function) {
			super("Function " + function + " not found");
			this.function = function;
		}
	}

	public static class NotCallableError extends ScriptError {
		public final Object function;
		public final Object evalFunction;
		public final Prototype prototype;

		public NotCallableError(Object function, Object evalFunction, Prototype prototype) {
			super("Cannot call " + function + ", " + evalFunction + " (" + prototype + ")" + " is not a function");
			this.function = function;
			this.evalFunction = evalFunction;
			this.prototype = prototype;
		}
	}

	public Object function;
	public Object[] arguments;
	public boolean isNew;

	@Override
	public void append(AstStringBuilder builder) {
		if (isNew) {
			builder.append("new ");
		}

		if (function instanceof AstFunction func && func.functionName != null) {
			builder.append(func.functionName);
		} else if (function instanceof AstGetScopeMember member) {
			builder.append(member.name);
		} else {
			builder.append("<unknown function>");
		}

		builder.append('(');

		for (int i = 0; i < arguments.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			builder.appendValue(arguments[i]);
		}

		builder.append(')');
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		var func = cx.eval(scope, function);

		if (Special.isInvalid(func)) {
			throw new FunctionNotFoundError(function);
		} else if (!(func instanceof Callable)) {
			throw new NotCallableError(function, func, cx.getPrototype(scope, func));
		}

		var args = ((Callable) func).convertArgs(cx, scope, arguments);

		var r = ((Callable) func).call(cx, scope, args);

		if (r == Special.NOT_FOUND) {
			throw new NotCallableError(function, func, cx.getPrototype(scope, func));
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