package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

import java.util.ArrayList;
import java.util.List;

public class AstList extends AstExpression {
	public static class SpreadError extends ScriptError {
		public SpreadError() {
			super("Spread used on non-array");
		}
	}

	public final List<Object> values;

	public AstList(List<Object> v) {
		values = v;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('[');

		for (int i = 0; i < values.size(); i++) {
			if (i > 0) {
				builder.append(',');
			}

			builder.appendValue(values.get(i));
		}

		builder.append(']');
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		var list = new ArrayList<>(values.size());

		for (var o : values) {
			if (o instanceof AstSpread spread) {
				var s = cx.eval(scope, spread.value);

				if (s instanceof Iterable<?> itr) {
					for (var o1 : itr) {
						list.add(o1);
					}
				} else {
					throw new SpreadError().pos(pos);
				}
			} else {
				list.add(cx.eval(scope, o));
			}
		}

		return list;
	}

	@Override
	public Object optimize(Parser parser) {
		for (var o : values) {
			if (o instanceof Evaluable) {
				return this;
			}
		}

		return values;
	}
}
