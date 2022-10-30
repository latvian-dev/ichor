package dev.latvian.apps.ichor.parser.expression.unary;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstNegate extends AstUnary {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append('-');
		builder.append(node);
	}

	@Override
	public Object eval(Scope scope) {
		return evalBoolean(scope);
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return !scope.getContext().asBoolean(scope, node);
	}

	@Override
	public int evalInt(Scope scope) {
		return !scope.getContext().asBoolean(scope, node) ? 0 : 1;
	}
}
