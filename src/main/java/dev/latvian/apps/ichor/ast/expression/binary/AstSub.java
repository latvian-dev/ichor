package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;

public class AstSub extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('-');
	}

	@Override
	public Object eval(Scope scope) {
		return evalDouble(scope);
	}

	@Override
	public double evalDouble(Scope scope) {
		return scope.asDouble(left) - scope.asDouble(right);
	}

	@Override
	public Object optimize(Parser parser) {
		super.optimize(parser);

		if (left instanceof Number l && right instanceof Number r) {
			return l.doubleValue() - r.doubleValue();
		}

		return this;
	}
}
