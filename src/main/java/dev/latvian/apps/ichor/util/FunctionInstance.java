package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.CallableTypeAdapter;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.error.ArgumentCountMismatchError;
import dev.latvian.apps.ichor.error.ConstructorError;
import dev.latvian.apps.ichor.exit.ReturnExit;

import java.util.concurrent.CompletableFuture;

public class FunctionInstance implements CallableTypeAdapter {
	public final AstFunction function;
	public final Scope evalScope;

	public FunctionInstance(AstFunction function, Scope evalScope) {
		this.function = function;
		this.evalScope = evalScope;
	}

	@Override
	public Object call(Scope callScope, Object[] args, boolean hasNew) {
		if (hasNew) {
			throw new ConstructorError(null);
		} else if (args.length < function.requiredParams) {
			throw new ArgumentCountMismatchError(function.requiredParams, args.length).pos(function.pos);
		}

		var s = evalScope.push(this);

		try {
			s.scopeArguments = new Object[function.params.length];

			if (!(function.hasMod(AstFunction.Mod.ARROW) || function.hasMod(AstFunction.Mod.CLASS))) {
				s.setScopeThis(s);
			}

			for (int i = 0; i < function.params.length; i++) {
				Object value;

				if (i >= args.length) {
					if (function.params[i].defaultValue == Special.UNDEFINED) {
						value = Special.UNDEFINED;
					} else {
						value = s.eval(function.params[i].defaultValue);
					}
				} else {
					value = args[i];
				}

				s.scopeArguments[i] = value;
				s.addMutable(function.params[i].name, value);
			}

			function.body.interpretSafe(s);
		} catch (ReturnExit exit) {
			if (function.hasMod(AstFunction.Mod.ASYNC)) {
				return CompletableFuture.completedFuture(exit.value);
			}

			return exit.value;
		}

		return Special.UNDEFINED;
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
