package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;
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
		var m = scope.getMember(name);
		return m;
	}

	@Override
	public void set(Scope scope, Object value) {
		scope.setMember(name, value, AssignType.NONE);
	}
}
