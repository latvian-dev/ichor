package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.token.DeclaringToken;

public class AstMultiDeclareStatement extends AstDeclareStatement {
	public final Part[] parts;

	public AstMultiDeclareStatement(DeclaringToken assignToken, Part[] parts) {
		super(assignToken);
		this.parts = parts;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(assignToken);
		builder.append(' ');

		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			builder.append(parts[i]);
		}

		builder.append(';');
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		for (var p : parts) {
			p.declaration.declare(cx, scope, assignToken.flags, cx.eval(scope, p.value));
		}
	}

	@Override
	public void optimize(Parser parser) {
		for (var p : parts) {
			p.value = parser.optimize(p.value);
		}
	}
}
