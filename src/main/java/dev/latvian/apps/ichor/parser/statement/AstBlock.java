package dev.latvian.apps.ichor.parser.statement;

import java.util.Collection;

public class AstBlock extends AstStatement {
	public final AstStatement[] statements;

	public AstBlock(AstStatement... statements) {
		this.statements = statements;
	}

	public AstBlock(Collection<AstStatement> statements) {
		this(statements.toArray(EMPTY_STATEMENT_ARRAY));
	}

	@Override
	public void append(StringBuilder builder) {
		if (statements.length == 1) {
			builder.append('{');
			statements[0].append(builder);
			builder.append('}');
		} else {
			builder.append("{\n");

			for (var s : statements) {
				builder.append('\t');
				s.append(builder);
				builder.append('\n');
			}

			builder.append('}');
		}
	}
}
