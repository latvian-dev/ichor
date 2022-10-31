package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstGetByEvaluable extends AstGetFrom {
	public final Evaluable key;

	public AstGetByEvaluable(Evaluable from, Evaluable key) {
		super(from);
		this.key = key;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);
		builder.append('[');
		builder.append(key);
		builder.append(']');
	}

	@Override
	public Object eval(Scope scope) {
		var o = from.eval(scope);
		var p = scope.getContext().getPrototype(o);
		var k = key.eval(scope);

		if (k instanceof Number) {
			return p.get(scope, ((Number) k).intValue(), o);
		} else {
			return p.get(scope, scope.getContext().asString(scope, k), o);
		}
	}

	@Override
	public void set(Scope scope, Object value) {
		var o = from.eval(scope);
		var p = scope.getContext().getPrototype(o);
		var k = key.eval(scope);

		if (k instanceof Number) {
			p.set(scope, ((Number) k).intValue(), o, value);
		} else {
			p.set(scope, scope.getContext().asString(scope, k), o, value);
		}
	}
}
