package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.Ast;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Evaluable;

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
	}

	@Override
	public void interpret(Scope scope) {
		expression.eval(scope);
	}
}
