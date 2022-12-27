package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstSpread extends AstExpression {
	public Object value;

	public AstSpread(Object v) {
		value = v;
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return cx.eval(scope, value);
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("...");
		builder.appendValue(value);
	}
}
