package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.CallableTypeAdapter;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.error.ConstructorError;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.exit.ReturnExit;

import java.util.concurrent.CompletableFuture;

public class FunctionInstance implements CallableTypeAdapter {
	public static class ArgumentCountMismatchError extends ScriptError {
		public final int requiredCount;
		public final int actualCount;

		public ArgumentCountMismatchError(int requiredCount, int actualCount) {
			super("Invalid number of arguments: " + actualCount + " < " + requiredCount);
			this.requiredCount = requiredCount;
			this.actualCount = actualCount;
		}
	}

	public final AstFunction function;
	public final Context evalContext;
	public final Scope evalScope;

	public FunctionInstance(AstFunction function, Context evalContext, Scope evalScope) {
		this.function = function;
		this.evalContext = evalContext;
		this.evalScope = evalScope;
	}

	@Override
	public Object call(Context cx, Scope callScope, Object[] args, boolean hasNew) {
		if (hasNew) {
			throw new ConstructorError(null);
		} else if (args.length < function.requiredParams) {
			throw new ArgumentCountMismatchError(function.requiredParams, args.length).pos(function.pos);
		}

		var s = evalScope.push(this);

		try {
			s.scopeArguments = new Object[function.params.length];

			for (int i = 0; i < function.params.length; i++) {
				Object value;

				if (i >= args.length) {
					if (function.params[i].defaultValue == Special.UNDEFINED) {
						value = Special.UNDEFINED;
					} else {
						value = evalContext.eval(s, function.params[i].defaultValue);
					}
				} else {
					value = args[i];
				}

				s.scopeArguments[i] = value;
				s.addMutable(function.params[i].name, value);
			}

			function.body.interpretSafe(evalContext, s);
		} catch (ReturnExit exit) {
			if (function.hasMod(AstFunction.Mod.ASYNC)) {
				return CompletableFuture.completedFuture(exit.value);
			}

			return exit.value;
		}

		return Special.UNDEFINED;
	}

	@Override
	public Context getEvalContext() {
		return evalContext;
	}

	@Override
	public Scope getEvalScope() {
		return evalScope;
	}

	@Override
	public String toString() {
		return "FunctionProxy[" + function + "]";
	}

	@Override
	public int hashCode() {
		return function.hashCode();
	}
}
