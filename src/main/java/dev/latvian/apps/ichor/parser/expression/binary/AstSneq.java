package dev.latvian.apps.ichor.parser.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstSneq extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("!==");
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return !AstSeq.shallowEquals(evalL(scope), evalR(scope));
	}
}
