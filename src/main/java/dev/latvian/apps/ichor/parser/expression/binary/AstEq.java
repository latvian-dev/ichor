package dev.latvian.apps.ichor.parser.expression.binary;

import dev.latvian.apps.ichor.Scope;

import java.util.Objects;

public class AstEq extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("==");
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return Objects.equals(evalL(scope), evalR(scope));
	}
}
