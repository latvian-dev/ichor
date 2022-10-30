package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Interpreter;
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
		for (int i = 0; i < interpretable.length; i++) {
			if (i > 0) {
				builder.append(";");
			}

			builder.append(' ');
			builder.append(interpretable[i]);
		}

		builder.append(' ');
	}

	@Override
	public void interpret(Interpreter interpreter) {
		for (var statement : interpretable) {
			statement.interpret(interpreter);
		}
	}
}
