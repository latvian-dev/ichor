package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

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
		for (var statement : interpretable) {
			builder.append(statement);
		}
	}

	@Override
	public void interpret(Scope scope) {
		for (var statement : interpretable) {
			statement.interpretSafe(scope);
		}
	}

	@Override
	public void optimize(Parser parser) {
		for (var statement : interpretable) {
			statement.optimize(parser);
		}
	}
}
