package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;

public class AstLabel extends AstStatement {
	public final String label;
	public final AstLabelledStatement statement;

	public AstLabel(String label, AstLabelledStatement statement) {
		this.label = label;
		this.statement = statement;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(label);
		builder.append(':');
		builder.append(statement);
	}

	@Override
	public void interpret(Scope scope) {
		try {
			statement.interpretSafe(scope);
		} catch (BreakExit exit) {
			if (!exit.label.equals(label)) {
				throw exit;
			}
		}
	}
}
