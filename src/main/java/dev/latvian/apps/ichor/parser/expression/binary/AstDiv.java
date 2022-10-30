package dev.latvian.apps.ichor.parser.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstDiv extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('/');
	}

	@Override
	public Object eval(Scope scope) {
		return scope.getContext().asDouble(scope, left) / scope.getContext().asDouble(scope, right);
	}
}
