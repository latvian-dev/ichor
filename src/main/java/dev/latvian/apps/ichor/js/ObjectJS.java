package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.java.MapValueHandler;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectJS {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("Object")
			.constructor((cx, args, hasNew) -> new LinkedHashMap<>())
			.asString((scope, self, builder) -> {
				builder.append('{');

				boolean first = true;

				for (var entry : map(self).entrySet()) {
					if (first) {
						first = false;
					} else {
						builder.append(',');
					}

					AstStringBuilder.wrapKey(entry.getKey(), builder);
					builder.append(':');
					scope.getContext().asString(scope, entry.getValue(), builder);
				}

				builder.append('}');
			})
			.namedValueHandler(MapValueHandler.INSTANCE);

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Map<String, Object> map(Object self) {
		return (Map) self;
	}
}
