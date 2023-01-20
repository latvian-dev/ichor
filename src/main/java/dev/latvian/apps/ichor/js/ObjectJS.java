package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.java.MapValueHandler;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectJS {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("Object") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return new LinkedHashMap<>();
		}

		@Override
		public void asString(Context cx, Scope scope, Object self, StringBuilder builder, boolean escape) {
			builder.append('{');

			boolean first = true;

			for (var entry : map(self).entrySet()) {
				if (first) {
					first = false;
				} else {
					builder.append(',');
					builder.append(' ');
				}

				AstStringBuilder.wrapKey(entry.getKey(), builder);
				builder.append(':');
				builder.append(' ');
				cx.asString(scope, entry.getValue(), builder, true);
			}

			builder.append('}');
		}
	}
			.namedValueHandler(MapValueHandler.INSTANCE);

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Map<String, Object> map(Object self) {
		return (Map) self;
	}
}
