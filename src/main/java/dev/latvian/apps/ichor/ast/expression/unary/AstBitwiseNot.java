package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

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
}
