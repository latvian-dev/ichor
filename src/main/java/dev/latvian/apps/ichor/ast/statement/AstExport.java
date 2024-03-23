package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstExport extends AstStatement {
	public final Object statement;

	public AstExport(Object s) {
		statement = s;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("export ");
		builder.appendValue(statement);
	}

	@Override
	public void interpret(Scope scope) {
		// TODO: Implement
	}
}
