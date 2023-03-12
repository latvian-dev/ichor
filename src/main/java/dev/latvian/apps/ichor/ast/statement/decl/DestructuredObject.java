package dev.latvian.apps.ichor.ast.statement.decl;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public record DestructuredObject(AstDeclaration[] parts, String rest, Set<String> ignoredRest) implements AstDeclaration {
	public static final DestructuredObject EMPTY_OBJECT = new DestructuredObject(AstDeclaration.EMPTY, "", Set.of());

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('{');

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

		builder.append('}');
	}

	@Override
	public void declare(Context cx, Scope scope, byte flags, Object value) {
		var p = cx.getPrototype(scope, value);

		for (var decl : parts) {
			decl.declare(cx, scope, flags, value);
		}

		if (!rest.isEmpty()) {
			// TODO: special case Map for efficiency
			var keys = p.keys(cx, scope, p.cast(value));

			if (keys != null) {
				var restObj = new LinkedHashMap<String, Object>(keys.size() - ignoredRest.size());

				for (var k : keys) {
					var ks = k.toString();

					if (!ignoredRest.contains(ks)) {
						restObj.put(ks, p.getInternal(cx, scope, value, ks));
					}
				}

				scope.add(rest, restObj, flags);
			} else {
				scope.add(rest, Map.of(), flags);
			}
		}
	}
}