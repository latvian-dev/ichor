package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.token.DoubleToken;

public class AstMul extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('*');
	}

	@Override
	public Object eval(Scope scope) {
		return evalDouble(scope);
	}

	@Override
	public double evalDouble(Scope scope) {
		return left.evalDouble(scope) * right.evalDouble(scope);
	}

	@Override
	public int evalInt(Scope scope) {
		return (int) evalDouble(scope);
	}

	@Override
	public Evaluable optimize(Parser parser) {
		var s = super.optimize(parser);

		if (s == this && left instanceof DoubleToken l && right instanceof DoubleToken r) {
			return DoubleToken.of(l.value * r.value);
		}

		return s;
	}
}
