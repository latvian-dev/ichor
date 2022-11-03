package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstSpread extends AstExpression {
	public final Object value;

	public AstSpread(Object v) {
		value = v;
	}

	@Override
	public Object eval(Scope scope) {
		return scope.eval(value);
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("...");
		builder.append(value);
	}
}
