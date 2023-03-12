package dev.latvian.apps.ichor.ast.statement.decl;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.NamedMemberNotFoundError;

public final class DestructuredObjectName implements AstDeclaration {
	public final String name;
	public String rename;
	public Object defaultValue;

	public DestructuredObjectName(String name) {
		this.name = name;
		this.rename = name;
		this.defaultValue = Special.UNDEFINED;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(name);

		if (!name.equals(rename)) {
			builder.append(':');
			builder.append(rename);
		}
	}

	@Override
	public void declare(Context cx, Scope scope, byte flags, Object value) {
		var p = cx.getPrototype(scope, value);
		var v = p.getInternal(cx, scope, value, name);

		if (v != Special.NOT_FOUND) {
			scope.add(rename, v, flags);
		} else if (defaultValue != Special.UNDEFINED) {
			scope.add(rename, defaultValue, flags);
		} else {
			throw new NamedMemberNotFoundError(name, p, value);
		}
	}
}