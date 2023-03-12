package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.ast.statement.decl.AstDeclaration;
import dev.latvian.apps.ichor.token.DeclaringToken;

public abstract class AstDeclareStatement extends AstStatement {
	public static class Part {
		public static final Part[] EMPTY = new Part[0];

		public final AstDeclaration declaration;
		public Object value;

		public Part(AstDeclaration declaration, Object value) {
			this.declaration = declaration;
			this.value = value;
		}
	}

	public final DeclaringToken assignToken;

	public AstDeclareStatement(DeclaringToken assignToken) {
		this.assignToken = assignToken;
	}
}
