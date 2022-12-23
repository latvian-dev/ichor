package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.token.DoubleToken;

public class AstBitwiseNot extends AstUnary {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append('~');
		builder.appendValue(node);
	}

	@Override
	public Object eval(Scope scope) {
		return evalInt(scope);
	}

	@Override
	public int evalInt(Scope scope) {
		return ~node.evalInt(scope);
	}

	@Override
	public double evalDouble(Scope scope) {
		return evalInt(scope);
	}

	@Override
	public Evaluable optimize(Parser parser) {
		node = node.optimize(parser);

		if (node instanceof DoubleToken n) {
			return DoubleToken.of(~((int) n.value));
		}

		return this;
	}
}
