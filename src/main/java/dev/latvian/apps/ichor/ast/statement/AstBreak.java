package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;

public class AstBreak extends AstStatement {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("break");
	}

	@Override
	public void interpret(Scope scope) {
		throw new BreakExit();
	}
}
