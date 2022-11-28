package dev.latvian.apps.ichor.ast.expression;

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
	public boolean delete(Scope scope) {
		var cx = scope.getContext();

		if (cx.debugger != null) {
			cx.debugger.delete(scope, this);
		}

		if (scope.deleteDeclaredMember(name) == Special.NOT_FOUND) {
			throw new ScriptError("Member " + name + " not found");
		}

		return true;
	}

	@Override
	public Evaluable createCall(Evaluable[] arguments, boolean isNew) {
		return new AstScopeMemberCall(name, arguments, isNew);
	}

	public static class AstScopeMemberCall extends AstCallBase {
		public final String name;

		public AstScopeMemberCall(String name, Evaluable[] arguments, boolean isNew) {
			super(arguments, isNew);
			this.name = name;
		}

		@Override
		public void append(AstStringBuilder builder) {
			builder.append(name);
			super.append(builder);
		}

		@Override
		public String calleeName() {
			return name;
		}

		@Override
		public Object evalFunc(Scope scope) {
			return scope.getMember(name);
		}
	}
}
