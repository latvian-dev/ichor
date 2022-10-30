package dev.latvian.apps.ichor.parser.expression.binary;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;

public class AstNc extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("??");
	}

	@Override
	public Object eval(Scope scope) {
		var l = evalL(scope);
		return Special.isInvalid(l) ? evalR(scope) : l;
	}
}
