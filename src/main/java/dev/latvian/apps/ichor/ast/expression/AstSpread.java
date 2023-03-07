package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstSpread extends AstTempExpression {
	public Object value;

	public AstSpread(Object v) {
		value = v;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("...");
		builder.appendValue(value);
	}
}
