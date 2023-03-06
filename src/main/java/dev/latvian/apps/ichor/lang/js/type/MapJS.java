package dev.latvian.apps.ichor.lang.js.type;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapJS extends Prototype<Map<?, ?>> {
	public MapJS(Context cx) {
		super(cx, "Map", Map.class);
	}

	@Override
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		return new LinkedHashMap<>();
	}

	@Override
	public boolean asString(Context cx, Scope scope, Map<?, ?> self, StringBuilder builder, boolean escape) {
		builder.append('{');

		boolean first = true;

		for (var entry : self.entrySet()) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
				builder.append(' ');
			}

			AstStringBuilder.wrapKey(String.valueOf(entry.getKey()), builder);
			builder.append(':');
			builder.append(' ');
			cx.asString(scope, entry.getValue(), builder, true);
		}

		builder.append('}');
		return true;
	}

	@Override
	@Nullable
	public Object getLocal(Context cx, Scope scope, Map<?, ?> self, String name) {
		var o = self.get(name);

		if (o != null) {
			return o == Special.NULL ? null : o;
		}

		return super.getLocal(cx, scope, self, name);
	}

	@Override
	public boolean setLocal(Context cx, Scope scope, Map<?, ?> self, String name, @Nullable Object value) {
		self.put(cast(name), cast(value == null ? Special.NULL : value)); // FIXME: Casting
		return true;
	}

	@Override
	public boolean deleteLocal(Context cx, Scope scope, Map<?, ?> self, String name) {
		return self.remove(name) != null;
	}

	@Override
	public Collection<?> keys(Context cx, Scope scope, Map<?, ?> self) {
		return self.keySet();
	}

	@Override
	public Collection<?> values(Context cx, Scope scope, Map<?, ?> self) {
		return self.values();
	}

	@Override
	public Collection<?> entries(Context cx, Scope scope, Map<?, ?> self) {
		if (self.isEmpty()) {
			return List.of();
		} else if (self.size() == 1) {
			var entry = self.entrySet().iterator().next();
			return List.of(List.of(entry.getKey(), entry.getValue()));
		} else {
			var entries = new Object[self.size()];
			int i = 0;

			for (var entry : self.entrySet()) {
				entries[i] = List.of(entry.getKey(), entry.getValue());
				i++;
			}

			return Arrays.asList(entries);
		}
	}
}
