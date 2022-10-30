package dev.latvian.apps.ichor.parser.expression.binary;

import dev.latvian.apps.ichor.Scope;

public class AstSetReplace extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('=');
	}

	@Override
	public Object eval(Scope scope) {

		return null;
	}
}
