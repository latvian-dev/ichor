package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;

public class AstFor extends AstLabeledStatement {
	public Interpretable initializer;
	public Evaluable condition;
	public Interpretable increment;
	public Interpretable body;

	@Override
	public void append(AstStringBuilder builder) {
		if (!label.isEmpty()) {
			builder.append(label);
			builder.append(':');
		}

		builder.append("for(");

		if (initializer != null) {
			builder.append(initializer);
		}

		builder.append(';');

		if (condition != null) {
			builder.append(condition);
		}

		builder.append(';');

		if (increment != null) {
			builder.append(increment);
		}

		builder.append(')');

		if (body != null) {
			builder.append(body);
		} else {
			builder.append(';');
		}
	}

	@Override
	public void interpret(Scope scope) {
		var s = scope.push();

		if (initializer != null) {
			initializer.interpretSafe(s);
		}

		while (condition == null || condition.evalBoolean(s)) {
			try {
				if (body != null) {
					body.interpretSafe(s);
				}
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

			if (increment != null) {
				increment.interpretSafe(s);
			}
		}
	}
}
