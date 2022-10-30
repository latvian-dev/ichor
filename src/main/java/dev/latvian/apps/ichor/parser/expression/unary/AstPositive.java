package dev.latvian.apps.ichor.parser.expression.unary;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstPositive extends AstUnary {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append('+');
		builder.append(node);
	}

	@Override
	public Object eval(Scope scope) {
		throw new ScriptError("Something didn't call optimize()");
	}

	@Override
	public Object optimize() {
		return node;
	}
}
