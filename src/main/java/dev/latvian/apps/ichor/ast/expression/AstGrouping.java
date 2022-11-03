package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Evaluable;

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
