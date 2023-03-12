package dev.latvian.apps.ichor.ast.statement.decl;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

import java.util.ArrayList;
import java.util.Collection;

public record DestructuredArray(AstDeclaration[] parts, String rest, int restIndex) implements AstDeclaration {
	public static final DestructuredArray EMPTY_ARRAY = new DestructuredArray(AstDeclaration.EMPTY, "", 0);

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('[');

		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			parts[i].append(builder);
		}

		if (!rest.isEmpty()) {
			if (parts.length > 0) {
				builder.append(',');
			}

			builder.append("...");
			builder.append(rest);
		}

		builder.append(']');
	}

	@Override
	public void declare(Context cx, Scope scope, byte flags, Object value) {
		var p = cx.getPrototype(scope, value);

		for (var decl : parts) {
			decl.declare(cx, scope, flags, value);
		}

		if (!rest.isEmpty()) {
			int len = value instanceof Collection<?> c ? c.size() : p.getLength(cx, scope, value);
			var restArr = new ArrayList<>();

			for (int i = restIndex; i < len; i++) {
				restArr.add(p.getLocal(cx, scope, p.cast(value), i));
			}

			scope.add(rest, restArr, flags);
		}
	}
}