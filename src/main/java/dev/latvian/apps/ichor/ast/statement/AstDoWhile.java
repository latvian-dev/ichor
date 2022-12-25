package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;

public class AstDoWhile extends AstWhile {
	@Override
	public void append(AstStringBuilder builder) {
		if (!label.isEmpty()) {
			builder.append(label);
			builder.append(':');
		}

		builder.append("do ");

		if (body != null) {
			builder.append(body);
		} else {
			builder.append(';');
		}

		builder.append("while (");
		builder.append(condition);
		builder.append(')');
	}

	@Override
	public void interpret(Scope scope) {
		do {
			if (body != null) {
				try {
					body.interpretSafe(scope);
				} catch (BreakExit exit) {
					if (exit.stop == this) {
						break;
					} else {
						throw exit;
					}
				} catch (ContinueExit exit) {
					if (exit.stop != this) {
						throw exit;
					}
				}
			}
		}
		while (condition.evalBoolean(scope));
	}
}
