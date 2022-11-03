package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Debugger;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class ConsoleDebugger implements Debugger {
	public String indent = "[DEBUG] ";

	private String wrapString(Object o) {
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
		System.out.println(indent + "* -> " + scope);
		indent += "  ";
	}

	@Override
	public void popScope(Scope scope) {
		indent = indent.substring(0, indent.length() - 2);
		System.out.println(indent + "* <- " + scope);
	}

	@Override
	public void pushSelf(Object self) {
		System.out.println(indent + "* Self -> " + self);
	}

	@Override
	public void get(Object object, Object returnValue) {
		System.out.println(indent + "* Get @ " + object + " = " + wrapString(returnValue));
	}

	@Override
	public void set(Object object, Object value) {
		System.out.println(indent + "* Get @ " + object + " = " + wrapString(value));
	}

	@Override
	public void call(Object callee, Object[] args, Object returnValue) {
		var sb = new AstStringBuilder();
		sb.append(indent);
		sb.append("* Call => ");
		sb.append(callee);
		sb.append('(');

		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				sb.append(',');
			}

			sb.appendValue(args[i]);
		}

		sb.append(") = ");
		sb.append(wrapString(returnValue));

		System.out.println(sb);
	}

	@Override
	public void assignNew(Object object, Object value) {
		if (value instanceof PrototypeBuilder) {
			return;
		}

		System.out.println(indent + "* Assign New @ " + object + " = " + wrapString(value));
	}

	@Override
	public void assignSet(Object object, Object value) {
		System.out.println(indent + "* Assign Set @ " + object + " = " + wrapString(value));
	}
}
