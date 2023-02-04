package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.token.DeclaringToken;

public class AstSingleDeclareStatement extends AstDeclareStatement {
	public final AstDeclaration variable;

	public AstSingleDeclareStatement(DeclaringToken assignToken, AstDeclaration variable) {
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
		variable.declare(cx, scope, assignToken.flags);
	}

	@Override
	public void optimize(Parser parser) {
		variable.optimize(parser);
	}
}
