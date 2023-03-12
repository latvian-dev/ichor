package dev.latvian.apps.ichor.ast.statement.decl;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.IndexedMemberNotFoundError;

public record DestructuredArrayName(String name, int index) implements AstDeclaration {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append(name);
	}

	@Override
	public void declare(Context cx, Scope scope, byte flags, Object value) {
		var p = cx.getPrototype(scope, value);
		var v = p.getLocal(cx, scope, p.cast(value), index);

		if (v != Special.NOT_FOUND) {
			scope.add(name, v, flags);
		} else {
			throw new IndexedMemberNotFoundError(index, p, value);
		}
	}
}