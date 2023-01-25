package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.java.JavaClassPrototype;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapJS extends JavaObjectJS<Map<String, ?>> {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("Object") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return new LinkedHashMap<>();
		}
	};

	public MapJS(Map<String, ?> object, JavaClassPrototype prototype) {
		super(object, prototype);
	}

	@Override
	public void asString(Context cx, Scope scope, StringBuilder builder, boolean escape) {
		builder.append('{');

		boolean first = true;

		for (var entry : self.entrySet()) {
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

	@Override
	public Collection<?> keys(Context cx, Scope scope) {
		return self.keySet();
	}

	@Override
	public Collection<?> values(Context cx, Scope scope) {
		return self.values();
	}

	@Override
	public Collection<?> entries(Context cx, Scope scope) {
		if (self.isEmpty()) {
			return List.of();
		} else if (self.size() == 1) {
			var entry = self.entrySet().iterator().next();
			return List.of(List.of(entry.getKey(), entry.getValue()));
		} else {
			var entries = new ArrayList<List<Object>>(self.size());

			for (var entry : self.entrySet()) {
				entries.add(List.of(entry.getKey(), entry.getValue()));
			}

			return entries;
		}
	}
}
