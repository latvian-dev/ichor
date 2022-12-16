package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.InterfaceFactory;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.CallableAst;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.exit.ReturnExit;
import dev.latvian.apps.ichor.exit.ScopeExit;
import dev.latvian.apps.ichor.prototype.PrototypeFunction;
import dev.latvian.apps.ichor.util.AssignType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

public class AstFunction extends AstExpression implements PrototypeFunction, Comparable<AstFunction>, CallableAst, InterfaceFactory {
	public static final int MOD_ARROW = 1;
	public static final int MOD_CLASS = 2;
	public static final int MOD_STATIC = 4;
	public static final int MOD_SET = 8;
	public static final int MOD_GET = 16;
	public static final int MOD_CONSTRUCTOR = 32;
	public static final int MOD_ASYNC = 64;
	public static final int MOD_VARARGS = 128;

	private record FunctionInvocationHandler(Context cx, AstFunction func) implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			System.out.println("Invoking " + method + " of " + func);

			return switch (method.getName()) {
				case "toString" -> func.toString();
				case "hashCode" -> func.hashCode();
				case "equals" -> func.equals(args[0]);
				// default -> func.call(cx, args);
				default -> null;
			};
		}
	}

	public final AstParam[] params;
	public final Interpretable body;
	public final int modifiers;
	public final int requiredParams;
	public String functionName;

	public AstFunction(AstParam[] params, Interpretable body, int modifiers) {
		this.params = params;
		this.body = body;
		this.modifiers = modifiers;

		int requiredParams0 = params.length;

		for (int i = params.length - 1; i >= 0; i--) {
			if (params[i].defaultValue != Special.UNDEFINED) {
				requiredParams0--;
			} else {
				break;
			}
		}

		this.requiredParams = requiredParams0;
	}

	@Override
	public String getPrototypeName() {
		if (functionName != null) {
			return functionName;
		}

		return "<function>";
	}

	public boolean hasMod(int mod) {
		return (modifiers & mod) != 0;
	}

	@Override
	public Object eval(Scope scope) {
		return this;
	}

	@Override
	public void append(AstStringBuilder builder) {
		if (hasMod(MOD_ASYNC)) {
			builder.append("async ");
		}

		if (!hasMod(MOD_ARROW)) {
			builder.append("function");

			if (functionName != null) {
				builder.append(' ');
				builder.append(functionName);
			}
		}

		builder.append('(');

		for (int i = 0; i < params.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			if (i == params.length - 1 && hasMod(MOD_VARARGS)) {
				builder.append("...");
			}

			params[i].append(builder);
		}

		builder.append(")");

		if (hasMod(MOD_ARROW)) {
			builder.append("=>");
		}

		builder.append(body);
	}

	@Override
	public Object call(Scope scope, Object self, Evaluable[] args) {
		if (args.length < requiredParams) {
			throw new ScriptError("Invalid number of arguments: " + args.length + " < " + requiredParams);
		}

		var s = scope.push(this);

		try {
			for (int i = 0; i < params.length; i++) {
				s.declareMember(params[i].name, i >= args.length ? params[i].defaultValue == Special.UNDEFINED ? Special.UNDEFINED : params[i].defaultValue.eval(scope) : args[i].eval(scope), AssignType.MUTABLE);
			}

			body.interpret(s);
		} catch (ReturnExit exit) {
			if (hasMod(MOD_ASYNC)) {
				return CompletableFuture.completedFuture(exit.value);
			}

			return exit.value;
		} catch (ScopeExit exit) {
			throw new ScriptError(exit);
		}

		return Special.UNDEFINED;
	}

	@Override
	public int compareTo(AstFunction o) {
		return Integer.compare(o.params.length, params.length);
	}

	@Override
	public Evaluable createCall(Evaluable[] arguments, boolean isNew) {
		return new AstFunctionCall(this, arguments, isNew);
	}

	private static class AstFunctionCall extends AstCallBase {
		private final AstFunction function;

		public AstFunctionCall(AstFunction function, Evaluable[] arguments, boolean isNew) {
			super(arguments, isNew);
			this.function = function;
		}

		@Override
		public String calleeName() {
			return function.toString();
		}

		@Override
		public Object evalFunc(Scope scope) {
			return function;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T createInterface(Context cx, Class<T> type) {
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new FunctionInvocationHandler(cx, this));
	}
}
