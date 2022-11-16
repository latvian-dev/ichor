package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstSpread extends AstExpression {
	public final Evaluable value;

	public AstSpread(Evaluable v) {
		value = v;
	}

	@Override
	public Object eval(Scope scope) {
		return value.eval(scope);
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("...");
		builder.append(value);
	}
}
