package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.Ast;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstExpressionStatement extends AstStatement {
	public final Evaluable expression;

	public AstExpressionStatement(Evaluable expression) {
		this.expression = expression;

		if (expression instanceof Ast) {
			pos((Ast) expression);
		}
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(expression);
		builder.append(';');
	}

	@Override
	public void interpret(Scope scope) {
		expression.eval(scope);
	}
}
