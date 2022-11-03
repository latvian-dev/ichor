package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

public abstract class AstBinaryBoolean extends AstBinary {
	@Override
	public Object eval(Scope scope) {
		return evalBoolean(scope);
	}

	@Override
	public abstract boolean evalBoolean(Scope scope);
}
