package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.statement.decl.AstDeclaration;
import dev.latvian.apps.ichor.token.DeclaringToken;

public class AstSingleDeclareStatement extends AstDeclareStatement {
	public final AstDeclaration declaration;
	public Object value;

	public AstSingleDeclareStatement(DeclaringToken assignToken, AstDeclaration declaration, Object value) {
		super(assignToken);
		this.declaration = declaration;
		this.value = value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(assignToken);
		builder.append(' ');
		declaration.append(builder);
		builder.append('=');
		builder.appendValue(value);
		builder.append(';');
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		declaration.declare(cx, scope, assignToken.flags, cx.eval(scope, value));
	}

	@Override
	public void optimize(Parser parser) {
		value = parser.optimize(value);
	}
}
