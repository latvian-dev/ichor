package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Adaptable;
import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.exit.ReturnExit;
import dev.latvian.apps.ichor.exit.ScopeExit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

public class FunctionInstance implements Callable, Adaptable, InvocationHandler {
	public final AstFunction function;
	public final Context evalContext;
	public final Scope evalScope;

	public FunctionInstance(AstFunction function, Context evalContext, Scope evalScope) {
		this.function = function;
		this.evalContext = evalContext;
		this.evalScope = evalScope;
	}

	@Override
	public Object call(Context cx, Scope callScope, Object self, Object[] args) {
		if (args.length < function.requiredParams) {
			throw new ScriptError("Invalid number of arguments: " + args.length + " < " + function.requiredParams).pos(function.pos);
		}

		var s = evalScope.push(this);

		try {
			for (int i = 0; i < function.params.length; i++) {
				Object value;

				if (i >= args.length) {
					if (function.params[i].defaultValue == Special.UNDEFINED) {
						value = Special.UNDEFINED;
					} else {
						value = evalContext.eval(evalScope, function.params[i].defaultValue);
					}
				} else {
					value = args[i];
				}

				s.declareMember(function.params[i].name, value, AssignType.MUTABLE);
			}

			function.body.interpretSafe(evalContext, s);
		} catch (ReturnExit exit) {
			if (function.hasMod(AstFunction.MOD_ASYNC)) {
				return CompletableFuture.completedFuture(exit.value);
			}

			return exit.value;
		} catch (ScopeExit exit) {
			throw new ScriptError(exit);
		}

		return Special.UNDEFINED;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T adapt(Context cx, Class<T> type) {
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		System.out.println("Invoking " + method + " of " + this);

		return switch (method.getName()) {
			case "toString" -> "Proxy[" + function + "]";
			case "hashCode" -> function.hashCode();
			case "equals" -> proxy == args[0];
			default -> call(evalContext, evalScope, proxy, args);
		};
	}

	@Override
	public String toString() {
		return function.toString();
	}
}
