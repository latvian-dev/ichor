package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.token.DeclaringToken;

public abstract class AstDeclareStatement extends AstStatement {
	public final DeclaringToken assignToken;

	public AstDeclareStatement(DeclaringToken assignToken) {
		this.assignToken = assignToken;
	}
}
