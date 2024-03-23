package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstExpressionStatement extends AstStatement {
	public Object expression;

	public AstExpressionStatement(Object expression) {
		this.expression = expression;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(expression);
		builder.append(';');
	}

	@Override
	public void interpret(Scope scope) {
		scope.eval(expression);
	}

	@Override
	public void optimize(Parser parser) {
		expression = parser.optimize(expression);
	}
}
