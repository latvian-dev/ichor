package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstBlock extends AstLabelledStatement {
	public final Interpretable[] interpretable;

	public AstBlock(Interpretable[] statements) {
		this.interpretable = statements;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('{');

		for (Interpretable value : interpretable) {
			builder.append(value);
		}

		builder.append('}');
	}

	@Override
	public void interpret(Scope scope) {
		var s = scope.push();

		for (var statement : interpretable) {
			statement.interpretSafe(s);
		}
	}
}
