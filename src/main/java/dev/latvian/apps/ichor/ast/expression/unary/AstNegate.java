package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstNegate extends AstUnary {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append('-');
		builder.appendValue(node);
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return evalDouble(cx, scope);
	}

	@Override
	public double evalDouble(Context cx, Scope scope) {
		return -cx.asDouble(scope, node);
	}

	@Override
	public int evalInt(Context cx, Scope scope) {
		return -cx.asInt(scope, node);
	}

	@Override
	public Object optimize(Parser parser) {
		super.optimize(parser);

		if (node instanceof Number n) {
			return -n.doubleValue();
		}

		return this;
	}
}
