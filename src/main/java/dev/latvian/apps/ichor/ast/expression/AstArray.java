package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Callable;
import dev.latvian.apps.ichor.prototype.Evaluable;

import java.util.ArrayList;
import java.util.List;

public class AstArray extends AstExpression {
	public final List<Object> values;
	private boolean eval;

	public AstArray(List<Object> v) {
		values = v;

		for (Object o : values) {
			if (o instanceof Evaluable && !(o instanceof Callable)) {
				eval = true;
				break;
			}
		}
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
		if (eval) {
			var list = new ArrayList<>(values.size());

			for (var o : values) {
				if (o instanceof AstSpread spread) {
					var s = scope.eval(spread.value);

					if (s instanceof Iterable<?> itr) {
						for (var o1 : itr) {
							list.add(o1);
						}
					} else {
						list.add(s);
					}
				} else {
					list.add(scope.eval(o));
				}
			}

			return list;
		}

		return values;
	}
}
