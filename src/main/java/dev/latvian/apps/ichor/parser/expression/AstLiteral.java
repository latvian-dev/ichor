package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstLiteral extends AstExpression {
	public final Object value;

	public AstLiteral(Object value) {
		this.value = value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(value);
	}

	@Override
	public Object eval(Scope scope) {
		return value;
	}

	@Override
	public Object optimize() {
		return value;
	}
}
