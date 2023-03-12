package dev.latvian.apps.ichor.ast.statement.decl;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstType;

public class NameDeclaration implements AstDeclaration {
	public final String name;
	public AstType type;

	public NameDeclaration(String name) {
		this.name = name;
		this.type = null;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(name);

		if (type != null) {
			builder.append(':');
			builder.append(type);
		}
	}

	@Override
	public void declare(Context cx, Scope scope, byte flags, Object value) {
		if (type != null) {
			scope.add(name, type.cast(cx, scope, value), flags);
		} else {
			scope.add(name, value, flags);
		}
	}
}