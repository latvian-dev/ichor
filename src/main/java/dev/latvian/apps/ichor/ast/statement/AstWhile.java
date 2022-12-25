package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;

public class AstWhile extends AstLabeledStatement {
	public Evaluable condition;
	public Interpretable body;

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("while (");
		builder.append(condition);
		builder.append(')');

		if (body != null) {
			builder.append(body);
		} else {
			builder.append(';');
		}
	}

	@Override
	public void interpret(Scope scope) {
		while (condition.evalBoolean(scope)) {
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
	}
}
