package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ReturnExit;

public class AstEmptyBlock extends AstStatement {
	public final boolean forceReturn;

	public AstEmptyBlock(boolean forceReturn) {
		this.forceReturn = forceReturn;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('{');
		builder.append('}');
	}

	@Override
	public void interpret(Scope scope) {
		if (forceReturn) {
			throw ReturnExit.DEFAULT_RETURN;
		}
	}
}
