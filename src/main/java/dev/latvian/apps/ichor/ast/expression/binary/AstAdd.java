package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstAdd extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('+');
	}

	@Override
	public Object eval(Scope scope) {
		if (left instanceof CharSequence || right instanceof CharSequence) {
			return evalString(scope);
		}

		return evalDouble(scope);
	}

	@Override
	public String evalString(Scope scope) {
		return scope.getContext().asString(scope, left) + scope.getContext().asString(scope, right);
	}

	@Override
	public double evalDouble(Scope scope) {
		return scope.getContext().asDouble(scope, left) + scope.getContext().asDouble(scope, right);
	}

	@Override
	public int evalInt(Scope scope) {
		return scope.getContext().asInt(scope, left) + scope.getContext().asInt(scope, right);
	}
}
