package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstThisStatement extends AstStatement {
	public final Object[] arguments;

	public AstThisStatement(Object[] a) {
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
	public void interpret(Context cx, Scope scope) {
	}

	@Override
	public void optimize(Parser parser) {
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = parser.optimize(arguments[i]);
		}
	}
}
