package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

public abstract class AstGetFrom extends AstGetBase {
	public final Evaluable from;

	public AstGetFrom(Evaluable from) {
		this.from = from;
	}

	public abstract Object evalKey(Scope scope);

	@Override
	public Evaluable createCall(Evaluable[] arguments, boolean isNew) {
		return new AstCall(this, arguments, isNew);
	}

	public static class AstCall extends AstExpression {
		public final AstGetFrom get;
		public final Evaluable[] arguments;
		public final boolean isNew;

		public AstCall(AstGetFrom get, Evaluable[] arguments, boolean isNew) {
			this.get = get;
			this.arguments = arguments;
			this.isNew = isNew;
		}

		@Override
		public void append(AstStringBuilder builder) {
			get.append(builder);
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
			var func = get.eval(scope);

			if (Special.isInvalid(func)) {
				throw new ScriptError("Cannot find " + get);
			} else if (!(func instanceof Callable)) {
				throw new ScriptError("Cannot call " + get + ", " + scope.getContext().toString(scope, func) + " (" + scope.getContext().getPrototype(func) + ")" + " is not a function");
			}

			var self = get.from.eval(scope);
			var cx = scope.getContext();

			if (cx.debugger != null) {
				cx.debugger.pushSelf(scope, self);
			}

			Object r;

			if (isNew) {
				r = ((Callable) func).construct(scope, arguments);
			} else {
				r = ((Callable) func).call(scope, self, arguments);
			}

			if (r == Special.NOT_FOUND) {
				throw new ScriptError("Cannot call " + get);
			}

			if (cx.debugger != null) {
				cx.debugger.call(scope, get, arguments, r);
			}

			return r;
		}
	}
}
