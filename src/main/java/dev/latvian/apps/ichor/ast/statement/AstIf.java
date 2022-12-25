package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ExitType;

public class AstIf extends AstLabeledStatement {
	public Evaluable condition;
	public Interpretable trueBody;
	public Interpretable falseBody;

	@Override
	public void append(AstStringBuilder builder) {
		if (!label.isEmpty()) {
			builder.append(label);
			builder.append(':');
		}

		builder.append("if (");
		builder.append(condition);
		builder.append(") ");

		if (trueBody != null) {
			builder.append(trueBody);
		} else {
			builder.append(';');
		}

		if (falseBody != null) {
			builder.append(" else ");
			builder.append(falseBody);
		}
	}

	@Override
	public boolean handle(ExitType type) {
		return type == ExitType.BREAK && !label.isEmpty();
	}

	@Override
	public void interpret(Scope scope) {
		try {
			if (condition.evalBoolean(scope)) {
				if (trueBody != null) {
					trueBody.interpretSafe(scope);
				}
			} else if (falseBody != null) {
				falseBody.interpretSafe(scope);
			}
		} catch (BreakExit exit) {
			if (exit.stop != this) {
				throw exit;
			}
		}
	}
}
