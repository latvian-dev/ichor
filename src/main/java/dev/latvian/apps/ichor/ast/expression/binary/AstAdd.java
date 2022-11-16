package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.token.StringToken;

public class AstAdd extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('+');
	}

	@Override
	public Object eval(Scope scope) {
		if (left instanceof StringToken || right instanceof StringToken) {
			return evalString(scope);
		}

		return evalDouble(scope);
	}

	@Override
	public String evalString(Scope scope) {
		return left.evalString(scope) + right.evalString(scope);
	}

	@Override
	public double evalDouble(Scope scope) {
		return left.evalDouble(scope) + right.evalDouble(scope);
	}

	@Override
	public int evalInt(Scope scope) {
		return left.evalInt(scope) + right.evalInt(scope);
	}
}
