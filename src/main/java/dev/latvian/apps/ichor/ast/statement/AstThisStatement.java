package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ScopeExit;

public class AstThisStatement extends AstStatement {
	public final Evaluable[] arguments;

	public AstThisStatement(Evaluable[] a) {
		this.arguments = a;
	}

	public String getStatementName() {
		return "this";
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(getStatementName());
		builder.append('(');

		for (int i = 0; i < arguments.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			builder.appendValue(arguments[i]);
		}

		builder.append(')');
	}

	@Override
	public void interpret(Scope scope) throws ScopeExit {
	}
}
