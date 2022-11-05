package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ScopeExit;

import java.util.Collection;
import java.util.List;

public class AstInterpretableGroup extends AstStatement {
	public static Interpretable optimized(List<Interpretable> statements) {
		return statements.size() == 1 ? statements.get(0) : new AstInterpretableGroup(statements);
	}

	public final Interpretable[] interpretable;

	public AstInterpretableGroup(Interpretable... statements) {
		this.interpretable = statements;
	}

	public AstInterpretableGroup(Collection<Interpretable> statements) {
		this(statements.toArray(Interpretable.EMPTY_INTERPRETABLE_ARRAY));
	}

	@Override
	public void append(AstStringBuilder builder) {
		for (Interpretable value : interpretable) {
			builder.append(value);
			builder.append(';');
		}
	}

	@Override
	public void interpret(Scope scope) throws ScopeExit {
		for (var statement : interpretable) {
			statement.interpret(scope);
		}
	}
}
