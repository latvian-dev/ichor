package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

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
		return scope.getMember(name);
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		scope.setMember(name, value);
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		scope.deleteDeclaredMember(name);
		return true;
	}

	@Override
	public Object optimize(Parser parser) {
		var slot = parser.getRootScope().getDeclaredMember(name);

		if (slot != null && slot.value != Special.UNDEFINED && slot.isRoot()) {
			return slot.value;
		}

		return this;
	}
}
