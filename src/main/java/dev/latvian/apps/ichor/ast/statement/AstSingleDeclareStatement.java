package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.token.DeclaringToken;
import dev.latvian.apps.ichor.util.AssignType;

public class AstSingleDeclareStatement extends AstDeclareStatement {
	public final AstParam variable;

	public AstSingleDeclareStatement(DeclaringToken assignToken, AstParam variable) {
		super(assignToken);
		this.variable = variable;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(assignToken);
		builder.append(' ');
		builder.append(variable);
		builder.append(';');
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		variable.declare(scope, cx.eval(scope, variable.defaultValue), assignToken.isConst() ? AssignType.IMMUTABLE : AssignType.MUTABLE);
	}

	@Override
	public void optimize(Parser parser) {
		variable.defaultValue = parser.optimize(variable.defaultValue);
	}
}
