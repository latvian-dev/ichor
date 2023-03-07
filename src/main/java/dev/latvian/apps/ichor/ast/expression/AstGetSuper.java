package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstGetSuper extends AstGetBase {
	public final String name;

	public AstGetSuper(String name) {
		this.name = name;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("super.");
		builder.append(name);
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return scope.scopeSuper.getMember(name);
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		scope.scopeSuper.setMember(name, value);
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		scope.scopeSuper.deleteDeclaredMember(name);
		return true;
	}
}
