package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.CallableAst;
import dev.latvian.apps.ichor.error.ScriptError;
import org.jetbrains.annotations.Nullable;

public abstract class AstCallBase extends AstExpression implements CallableAst {
	public final Evaluable[] arguments;
	public final boolean isNew;

	public AstCallBase(Evaluable[] arguments, boolean isNew) {
		this.arguments = arguments;
		this.isNew = isNew;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('(');

		for (int i = 0; i < arguments.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			builder.append(arguments[i]);
		}

		builder.append(')');
	}

	public abstract String calleeName();

	public abstract Object evalFunc(Scope scope);

	@Nullable
	public Object evalSelf(Scope scope) {
		return null;
	}

	@Override
	public Object eval(Scope scope) {
		var func = evalFunc(scope);

		if (Special.isInvalid(func)) {
			throw new ScriptError("Cannot find " + calleeName());
		} else if (!(func instanceof Callable)) {
			throw new ScriptError("Cannot call " + calleeName() + ", " + scope.getContext().toString(scope, func) + " (" + scope.getContext().getPrototype(func) + ")" + " is not a function");
		}

		var self = evalSelf(scope);
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
			throw new ScriptError("Cannot call " + calleeName());
		}

		if (cx.debugger != null) {
			cx.debugger.call(scope, calleeName(), arguments, r);
		}

		return r;
	}

	@Override
	public Evaluable createCall(Evaluable[] arguments, boolean isNew) {
		return new AstChainedCall(this, arguments, isNew);
	}

	public static class AstChainedCall extends AstCallBase {
		public final AstCallBase call;

		public AstChainedCall(AstCallBase call, Evaluable[] arguments, boolean isNew) {
			super(arguments, isNew);
			this.call = call;
		}

		@Override
		public void append(AstStringBuilder builder) {
			call.append(builder);
			super.append(builder);
		}

		@Override
		public String calleeName() {
			return call.toString();
		}

		@Override
		public Object evalFunc(Scope scope) {
			return call.eval(scope);
		}
	}
}