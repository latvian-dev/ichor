package dev.latvian.apps.ichor.ast.statement.decl;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.NamedMemberNotFoundError;

public final class NestedDestructuredPart implements AstDeclaration {
	private final String name;
	private final AstDeclaration part;

	public NestedDestructuredPart(String name, AstDeclaration part) {
		this.name = name;
		this.part = part;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(name);
		builder.append(':');
		part.append(builder);
	}

	@Override
	public void declare(Scope scope, byte flags, Object value) {
		var p = scope.getPrototype(value);
		var o = p.getInternal(scope, value, name);

		if (o != Special.NOT_FOUND) {
			part.declare(scope, flags, o);
		} else {
			throw new NamedMemberNotFoundError(name, p, value);
		}
	}
}