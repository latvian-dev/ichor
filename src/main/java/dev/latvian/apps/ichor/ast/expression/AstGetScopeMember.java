package dev.latvian.apps.ichor.ast.expression;

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
			throw new ScriptError("Cannot find " + this);
		}

		var cx = scope.getContext();

		if (cx.debugger != null) {
			cx.debugger.get(this, r);
		}

		return r;
	}

	@Override
	public void set(Scope scope, Object value) {
		scope.setMember(name, value, AssignType.NONE);

		var cx = scope.getContext();

		if (cx.debugger != null) {
			cx.debugger.set(this, value);
		}
	}
}
