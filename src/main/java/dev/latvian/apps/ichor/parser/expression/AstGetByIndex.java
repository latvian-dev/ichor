package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstGetByIndex extends AstGetFrom {
	public final int index;

	public AstGetByIndex(Evaluable from, int index) {
		super(from);
		this.index = index;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);
		builder.append('[');
		builder.append(index);
		builder.append(']');
	}

	@Override
	public Object eval(Scope scope) {
		var o = from.eval(scope);
		var p = scope.getContext().getPrototype(o);
		return p.get(scope, index, o);
	}

	@Override
	public void set(Scope scope, Object value) {
		var o = from.eval(scope);
		var p = scope.getContext().getPrototype(o);
		p.set(scope, index, o, value);
	}
}
