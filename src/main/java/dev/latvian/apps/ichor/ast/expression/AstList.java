package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

import java.util.ArrayList;
import java.util.List;

public class AstList extends AstExpression {
	public final List<Evaluable> values;

	public AstList(List<Evaluable> v) {
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
	public Object eval(Scope scope) {
		var list = new ArrayList<>(values.size());

		for (var o : values) {
			if (o instanceof AstSpread spread) {
				var s = spread.value.eval(scope);

				if (s instanceof Iterable<?> itr) {
					for (var o1 : itr) {
						list.add(o1);
					}
				} else {
					throw new ScriptError("Spread used on non-array").pos(pos);
				}
			} else {
				list.add(o.eval(scope));
			}
		}

		return list;
	}
}
