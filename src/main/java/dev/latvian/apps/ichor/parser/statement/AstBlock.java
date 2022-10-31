package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

import java.util.Collection;

public class AstBlock extends AstInterpretableGroup {

	public AstBlock(Collection<Interpretable> statements) {
		super(statements);
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('{');
		super.append(builder);
		builder.append('}');
	}

	@Override
	public void interpret(Scope scope) {
		var s = scope.push();

		try {
			for (var statement : interpretable) {
				statement.interpret(s);
			}
		} finally {
			scope.pop();
		}
	}

	@Override
	public void interpretNewScope(Scope scope) {
		interpret(scope);
	}
}
