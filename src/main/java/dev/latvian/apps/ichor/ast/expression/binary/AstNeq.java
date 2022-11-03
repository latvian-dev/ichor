package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Scope;

import java.util.Objects;

public class AstNeq extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("!=");
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return !Objects.equals(scope.eval(left), scope.eval(right));
	}
}
