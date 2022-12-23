package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ContinueExit;

public class AstContinue extends AstStatement {
	public final String label;

	public AstContinue(String label) {
		this.label = label;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("continue");

		if (!label.isEmpty()) {
			builder.append(" ");
			builder.append(label);
		}

		builder.append(';');
	}

	@Override
	public void interpret(Scope scope) {
		throw label.isEmpty() ? ContinueExit.DEFAULT_CONTINUE : new ContinueExit(label);
	}
}
