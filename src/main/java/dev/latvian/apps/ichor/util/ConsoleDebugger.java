package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Debugger;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class ConsoleDebugger implements Debugger {
	private String wrapString(Scope scope, Object o) {
		if (o == null) {
			return "null";
		} else if (o instanceof CharSequence) {
			return "\"" + o + "\"";
		} else {
			return o.toString();
		}
	}

	@Override
	public void pushScope(Scope scope) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Scope -> " + scope + " of " + scope.owner);
	}

	@Override
	public void pushSelf(Scope scope, Object self) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Self -> " + wrapString(scope, self));
	}

	@Override
	public void get(Scope scope, Object object, Object returnValue) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Get @ " + object + " = " + wrapString(scope, returnValue));
	}

	@Override
	public void set(Scope scope, Object object, Object value) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Get @ " + object + " = " + wrapString(scope, value));
	}

	@Override
	public void call(Scope scope, Object callee, Object[] args, Object returnValue) {
		var sb = new AstStringBuilder();
		sb.append("[DEBUG] ");
		sb.append("  ".repeat(scope.getDepth()));
		sb.append("* Call => ");
		sb.append(callee);
		sb.append('(');

		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				sb.append(',');
			}

			sb.appendValue(args[i]);
			sb.append('=');
			sb.append(wrapString(scope, args[i]));
		}

		sb.append(") = ");
		sb.append(wrapString(scope, returnValue));

		System.out.println(sb);
	}

	@Override
	public void assignNew(Scope scope, Object object, Object value) {
		if (value instanceof PrototypeBuilder) {
			return;
		}

		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Assign New @ " + object + " = " + wrapString(scope, value));
	}

	@Override
	public void assignSet(Scope scope, Object object, Object value) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Assign Set @ " + object + " = " + wrapString(scope, value));
	}

	@Override
	public void exit(Scope scope, Object value) {
		System.out.println("[DEBUG] " + "  ".repeat(scope.getDepth()) + "* Exit = " + wrapString(scope, value));
	}
}
