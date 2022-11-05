package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ReturnExit;
import dev.latvian.apps.ichor.exit.ScopeExit;

public class AstReturn extends AstStatement {
	public final Object value;

	public AstReturn(Object value) {
		this.value = value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("return ");
		builder.append(value);
	}

	@Override
	public void interpret(Scope scope) throws ScopeExit {
		throw new ReturnExit(scope.eval(value));
	}
}
