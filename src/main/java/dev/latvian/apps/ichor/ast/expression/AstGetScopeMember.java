package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
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
	public Object eval(Context cx, Scope scope) {
		var r = scope.getMember(name);

		if (r == Special.NOT_FOUND) {
			throw new ScriptError("Member " + name + " not found");
		}

		cx.debugger.get(cx, scope, this, r);
		return r;
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		cx.debugger.set(cx, scope, this, value);

		if (!scope.setMember(name, value, AssignType.NONE)) {
			throw new ScriptError("Member " + name + " not found");
		}
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		cx.debugger.delete(cx, scope, this);

		if (scope.deleteDeclaredMember(name) == Special.NOT_FOUND) {
			throw new ScriptError("Member " + name + " not found");
		}

		return true;
	}
}
