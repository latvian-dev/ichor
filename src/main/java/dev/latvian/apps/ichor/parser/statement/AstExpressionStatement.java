package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpreter;
import dev.latvian.apps.ichor.parser.Ast;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

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
	public void interpret(Interpreter interpreter) {
		expression.eval(interpreter.scope);
	}
}
