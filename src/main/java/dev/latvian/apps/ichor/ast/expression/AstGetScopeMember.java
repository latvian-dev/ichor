package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.util.AssignType;

public class AstGetScopeMember extends AstGetBase {
	public final String name;

	public AstGetScopeMember(String name) {
		this.name = name;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(name);
	}

	@Override
	public Object eval(Scope scope) {
		var r = scope.getMember(name);

		if (r == Special.NOT_FOUND) {
			throw new ScriptError("Member " + name + " not found");
		}

		var cx = scope.getContext();

		if (cx.debugger != null) {
			cx.debugger.get(scope, this, r);
		}

		return r;
	}

	@Override
	public void set(Scope scope, Object value) {
		var cx = scope.getContext();

		if (cx.debugger != null) {
			cx.debugger.set(scope, this, value);
		}

		if (!scope.setMember(name, value, AssignType.NONE)) {
			throw new ScriptError("Member " + name + " not found");
		}
	}

	@Override
	public Evaluable createCall(Object[] arguments, boolean isNew) {
		return new AstCall(name, arguments, isNew);
	}

	public static class AstCall extends AstExpression {
		public final String name;
		public final Object[] arguments;
		public final boolean isNew;

		public AstCall(String name, Object[] arguments, boolean isNew) {
			this.name = name;
			this.arguments = arguments;
			this.isNew = isNew;
		}

		@Override
		public void append(AstStringBuilder builder) {
			builder.append(name);
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
		public Object eval(Scope scope) {
			var func = scope.getMember(name);

			if (func == Special.NOT_FOUND) {
				throw new ScriptError("Cannot find " + name);
			} else if (!(func instanceof Callable)) {
				throw new ScriptError("Cannot call " + name);
			}

			var cx = scope.getContext();

			if (cx.debugger != null) {
				cx.debugger.pushSelf(scope, null);
			}

			Object r;

			if (isNew) {
				r = ((Callable) func).construct(scope, arguments);
			} else {
				r = ((Callable) func).call(scope, null, arguments);
			}

			if (r == Special.NOT_FOUND) {
				throw new ScriptError("Cannot call " + name);
			}

			if (cx.debugger != null) {
				cx.debugger.call(scope, name, arguments, r);
			}

			return r;
		}
	}
}
