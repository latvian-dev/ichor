package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstGetThis extends AstGetBase {
	public final String name;

	public AstGetThis(String name) {
		this.name = name;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("this.");
		builder.append(name);
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return scope.scopeThis.getMember(name);
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		scope.scopeThis.setMember(name, value);
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		scope.scopeThis.deleteDeclaredMember(name);
		return true;
	}
}
