package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.token.DeclaringToken;

public class AstMultiDeclareStatement extends AstDeclareStatement {
	public final AstDeclaration[] variables;

	public AstMultiDeclareStatement(DeclaringToken assignToken, AstDeclaration[] variables) {
		super(assignToken);
		this.variables = variables;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(assignToken);
		builder.append(' ');

		for (int i = 0; i < variables.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			builder.append(variables[i]);
		}

		builder.append(';');
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		for (var v : variables) {
			v.declare(cx, scope, assignToken.flags);
		}
	}

	@Override
	public void optimize(Parser parser) {
		for (var v : variables) {
			v.optimize(parser);
		}
	}
}
