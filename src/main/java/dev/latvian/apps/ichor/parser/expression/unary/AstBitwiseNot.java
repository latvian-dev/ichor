package dev.latvian.apps.ichor.parser.expression.unary;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstBitwiseNot extends AstUnary {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append('~');
		builder.append(node);
	}

	@Override
	public Object eval(Scope scope) {
		return evalInt(scope);
	}

	@Override
	public int evalInt(Scope scope) {
		return ~scope.getContext().asInt(scope, node);
	}
}
