package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ContinueExit;
import dev.latvian.apps.ichor.exit.ScopeExit;

public class AstContinue extends AstStatement {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("continue");
	}

	@Override
	public void interpret(Scope scope) throws ScopeExit {
		throw new ContinueExit();
	}
}
