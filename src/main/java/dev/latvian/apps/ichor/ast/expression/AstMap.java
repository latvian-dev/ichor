package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

import java.util.LinkedHashMap;
import java.util.Map;

public class AstMap extends AstExpression {
	public static class SpreadError extends ScriptError {
		public SpreadError() {
			super("Spread used on non-object");
		}
	}

	public final Map<String, Object> values;

	public AstMap(Map<String, Object> values) {
		this.values = values;
	}

	private static boolean isValidIdentifier(String s) {
		if (s.isEmpty()) {
			return false;
		}

		var chars = s.toCharArray();

		if (!Character.isJavaIdentifierStart(chars[0])) {
			return false;
		}

		for (int i = 1; i < s.length(); i++) {
			if (!Character.isJavaIdentifierPart(chars[i])) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('{');
		boolean first = true;

		for (var entry : values.entrySet()) {
			if (first) {
				first = false;
			} else {
				builder.append(',');
			}

			if (isValidIdentifier(entry.getKey())) {
				builder.append(entry.getKey());
			} else {
				builder.appendValue(entry.getKey());
			}

			builder.append(':');
			builder.appendValue(entry.getValue());
		}

		builder.append('}');
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		var map = new LinkedHashMap<>(values.size());

		for (var entry : values.entrySet()) {
			var o = entry.getValue();

			if (o instanceof AstSpread spread) {
				var s = cx.eval(scope, spread.value);

				if (s instanceof Map<?, ?> map1) {
					map.putAll(map1);
				} else {
					throw new SpreadError().pos(pos);
				}
			} else {
				map.put(entry.getKey(), cx.eval(scope, o));
			}
		}

		return map;
	}

	@Override
	public Object optimize(Parser parser) {
		for (var entry : values.entrySet()) {
			if (entry.getValue() instanceof Evaluable) {
				return this;
			}
		}

		return values;
	}
}
