package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Debugger;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstCall;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class ConsoleDebugger implements Debugger {
	public static final ConsoleDebugger INSTANCE = new ConsoleDebugger();

	private ConsoleDebugger() {
	}

	private String asString(Scope scope, Object value) {
		if (value instanceof CharSequence) {
			var sb = new StringBuilder();
			AstStringBuilder.wrapString(value, sb);
			return sb.toString();
		} else if (value instanceof AstFunction func) {
			var sb = new StringBuilder("function ");

			if (func.functionName != null) {
				sb.append(func.functionName);
			}

			sb.append("(");

			for (int i = 0; i < func.params.length; i++) {
				if (i > 0) {
					sb.append(',');
				}

				sb.append(func.params[i]);
			}

			sb.append(") {...}");
			return sb.toString();
		} else if (value instanceof FunctionInstance func) {
			return asString(scope, func.function);
		}

		return value == null ? "null" : value.toString();
	}

	@Override
	public void pushScope(Context cx, Scope scope) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Scope -> " + scope + " of " + asString(scope, scope.owner));
	}

	@Override
	public void pushSelf(Context cx, Scope scope, Object self) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Self -> " + asString(scope, self));
	}

	@Override
	public void get(Context cx, Scope scope, Object object, Object returnValue) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Get @ " + object + " = " + asString(scope, returnValue));
	}

	@Override
	public void set(Context cx, Scope scope, Object object, Object value) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Get @ " + object + " = " + asString(scope, value));
	}

	@Override
	public void delete(Context cx, Scope scope, Object object) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Delete @ " + asString(scope, object));
	}

	@Override
	public void call(Context cx, Scope scope, AstCall call, Object func, Object[] args, Object returnValue) {
		var sb = new AstStringBuilder();
		sb.append("[DEBUG] ");
		sb.append("  ".repeat(scope.getDepth()));
		sb.append("* Call => ");

		if (func instanceof FunctionInstance funcInst) {
			if (funcInst.function.functionName != null) {
				sb.append(funcInst.function.functionName);
			}

			sb.append("(");

			for (int i = 0; i < funcInst.function.params.length; i++) {
				if (i > 0) {
					sb.append(',');
				}

				sb.append(funcInst.function.params[i]);
				sb.append("==>");
				sb.append(i >= args.length ? "?" : asString(scope, args[i]));
			}

			sb.append(") {...}");

		} else {
			sb.append(call);
		}

		sb.append(func);
		sb.append('(');

		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				sb.append(',');
			}

			sb.append(args[i]);
			sb.append('=');
			sb.append(asString(scope, args[i]));
		}

		sb.append(") = ");
		sb.append(asString(scope, returnValue));

		System.out.println(sb);
	}

	@Override
	public void assignNew(Context cx, Scope scope, Object object, Object value) {
		if (value instanceof PrototypeBuilder) {
			return;
		}

		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Assign New @ " + object + " = " + asString(scope, value));
	}

	@Override
	public void assignSet(Context cx, Scope scope, Object object, Object value) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Assign Set @ " + object + " = " + asString(scope, value));
	}

	@Override
	public void exit(Context cx, Scope scope, Object value) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Exit = " + asString(scope, value));
	}
}
