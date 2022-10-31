package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

import java.util.Collection;
import java.util.List;

public class AstInterpretableGroup extends AstStatement {
	public final Interpretable[] interpretable;

	public static Interpretable optimized(List<Interpretable> statements) {
		return statements.size() == 1 ? statements.get(0) : new AstInterpretableGroup(statements);
	}

	public AstInterpretableGroup(Interpretable... statements) {
		this.interpretable = statements;
	}

	public AstInterpretableGroup(Collection<Interpretable> statements) {
		this(statements.toArray(Interpretable.EMPTY_INTERPRETABLE_ARRAY));
	}

	@Override
	public void append(AstStringBuilder builder) {
		for (Interpretable value : interpretable) {
			builder.append(' ');
			builder.append(value);
			builder.append(';');
		}

		builder.append(' ');
	}

	@Override
	public void interpret(Scope scope) {
		for (var statement : interpretable) {
			statement.interpret(scope);
		}
	}
}
