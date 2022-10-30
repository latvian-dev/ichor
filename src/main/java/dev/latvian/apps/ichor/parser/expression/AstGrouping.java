package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstGrouping extends AstExpression {
	public final Evaluable expression;

	public AstGrouping(Evaluable expression) {
		this.expression = expression;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('(');
		builder.append(expression);
		builder.append(')');
	}

	@Override
	public Object eval(Scope scope) {
		return expression.eval(scope);
	}
}
