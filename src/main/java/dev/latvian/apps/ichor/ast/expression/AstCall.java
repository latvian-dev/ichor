package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.binary.AstEq;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;

import java.util.Objects;

public class AstCall extends AstExpression {
	public static class FunctionNotFoundError extends ScriptError {
		public final Object function;

		public FunctionNotFoundError(Object function) {
			super("Function " + function + " not found");
			this.function = function;
		}
	}

	public static class CallError extends ScriptError {
		public final Object function;
		public final Object evalFunction;
		public final Prototype prototype;

		public CallError(Object function, Object evalFunction, Prototype prototype) {
			super("Cannot call " + (function == evalFunction ? function : (function + ", " + evalFunction)) + " of " + prototype);
			this.function = function;
			this.evalFunction = evalFunction;
			this.prototype = prototype;
		}
	}

	public Object function;
	public Object[] arguments;
	public boolean hasNew;

	@Override
	public void append(AstStringBuilder builder) {
		if (hasNew) {
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
			throw new CallError(function, func, cx.getPrototype(scope, func));
		}

		var args = ((Callable) func).convertArgs(cx, scope, arguments);
		var r = ((Callable) func).call(cx, scope, args, hasNew);

		if (r == Special.NOT_FOUND) {
			throw new CallError(function, func, cx.getPrototype(scope, func));
		}

		return r;
	}

	@Override
	public Object optimize(Parser parser) {
		function = parser.optimize(function);

		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = parser.optimize(arguments[i]);
		}

		if (function instanceof AstGetByName func) {
			if (arguments.length == 0) {
				if (func.name.equals("toString")) {
					return new AstToString(func.from);
				} else if (func.name.equals("hashCode")) {
					return new AstHashCode(func.from);
				}
			} else if (arguments.length == 1) {
				if (func.name.equals("equals")) {
					var ast = new AstEq();
					ast.left = func.from;
					ast.right = arguments[0];
					return ast.optimize(parser);
				}
			}
		}

		return this;
	}

	public static class AstToString extends AstExpression {
		public final Object from;

		public AstToString(Object from) {
			this.from = from;
		}

		@Override
		public Object eval(Context cx, Scope scope) {
			return cx.asString(scope, from, false);
		}

		@Override
		public void evalString(Context cx, Scope scope, StringBuilder builder) {
			cx.asString(scope, from, builder, false);
		}

		@Override
		public void append(AstStringBuilder builder) {
			builder.appendValue(from);
			builder.append(".toString()");
		}
	}

	public static class AstHashCode extends AstExpression {
		public final Object from;

		public AstHashCode(Object from) {
			this.from = from;
		}

		@Override
		public Object eval(Context cx, Scope scope) {
			return evalInt(cx, scope);
		}

		@Override
		public double evalDouble(Context cx, Scope scope) {
			return evalInt(cx, scope);
		}

		@Override
		public int evalInt(Context cx, Scope scope) {
			return Objects.hashCode(cx.eval(scope, from));
		}

		@Override
		public void append(AstStringBuilder builder) {
			builder.appendValue(from);
			builder.append(".hashCode()");
		}
	}
}