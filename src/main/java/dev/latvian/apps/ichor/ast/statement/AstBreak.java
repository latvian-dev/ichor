package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;

public class AstBreak extends AstStatement {
	public final String label;

	public AstBreak(String label) {
		this.label = label;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("break");

		if (!label.isEmpty()) {
			builder.append(" ");
			builder.append(label);
		}
	}

	@Override
	public void interpret(Scope scope) {
		throw new BreakExit(label);
	}
}
