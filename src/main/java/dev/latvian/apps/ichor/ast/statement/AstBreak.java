package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;

public class AstBreak extends AstStatement {
	public final LabeledStatement stop;

	public AstBreak(LabeledStatement stop) {
		this.stop = stop;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("break");

		if (stop != null) {
			builder.append(" ");
			builder.append(stop.getLabel());
		}

		builder.append(';');
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		throw new BreakExit(stop);
	}
}
